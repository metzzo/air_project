package indexer;

import preprocess.Preprocessor;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Indexer {
    private static Indexer instance = new Indexer();
    public static Indexer getInstance() {
        return Indexer.instance;
    }
    private Indexer() { }
    private static class RawDocument {
        String docNo;
        List<String> words;
    }

    class ReduceThread extends Thread {
        private final List<InvertedIndex> queue;
        private final int totalNum;

        ReduceThread(List<InvertedIndex> queue, int totalNum) {
            this.queue = queue;
            this.totalNum = totalNum;
        }

        @Override
        public void run() {
            InvertedIndex index1, index2;
            do {
                synchronized (this.queue) {
                    if (this.queue.size() > 1) {
                        index1 = this.queue.get(0);
                        this.queue.remove(0);

                        index2 = this.queue.get(0);
                        this.queue.remove(0);

                        System.out.println("Reducing, remaining size: " + this.queue.size() + " / " + this.totalNum);
                    } else {
                        index1 = null;
                        index2 = null;
                    }
                }

                if (index1 != null && index2 != null) {
                    index1.merge(index2);
                    synchronized (this.queue) {
                        this.queue.add(index1);
                    }
                }
            } while (index1 != null);
        }
    }

    class MapThread extends Thread {
        private final List<File> queue;
        private final List<InvertedIndex> indices;
        private final int totalNum;

        MapThread(List<File> queue, List<InvertedIndex> indices, int totalNum) {
            this.queue = queue;
            this.indices = indices;
            this.totalNum = totalNum;
        }

        @Override
        public void run() {
            File toProcess;
            do {
                synchronized (this.queue) {
                    if (this.queue.size() > 0) {
                        toProcess = this.queue.get(0);
                        this.queue.remove(0);

                        System.out.println("Indexing File " + toProcess.getPath() + " remaining files " + this.queue.size() + " / " + this.totalNum);
                    } else {
                        toProcess = null;
                    }
                }

                if (toProcess != null) {
                    InvertedIndex newIndex = Indexer.getInstance().indexFile(new DocumentRepository(), toProcess);
                    synchronized (this.indices) {
                        this.indices.add(newIndex);
                    }
                }
            } while (toProcess != null);
        }
    }

    public List<File> getFilesInDir(File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            throw new RuntimeException(folder.toString() + " does not exist or is a directory");
        }
        File[] files = Objects.requireNonNull(folder.listFiles());
        List<File> resultFiles = new LinkedList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                resultFiles.addAll(this.getFilesInDir(file));
            } else {
                resultFiles.add(file);
            }
        }
        return resultFiles;
    }

    public InvertedIndex index(List<File> files, int numThreads) {
        List<Thread> worker = new LinkedList<>();
        List<File> toIndex = new LinkedList<>(files);
        List<InvertedIndex> indices = new LinkedList<>();

        System.out.println("Map...");
        // map
        int numFiles = files.size();
        for (int i = 0; i < numThreads; i++) {
            MapThread t = new MapThread(toIndex, indices, numFiles);
            t.start();
            worker.add(t);
        }
        waitForThreads(worker);
        System.gc();
        worker.clear();

        System.out.println("Reduce...");
        // reduce
        int numIndices = indices.size();
        for (int i = 0; i < numThreads; i++) {
            ReduceThread t = new ReduceThread(indices, numIndices);
            t.start();
            worker.add(t);
        }
        waitForThreads(worker);

        InvertedIndex result = indices.get(0);
        System.out.println("Calculate Average Document Size ...");
        result.getDocumentRepository().calculateAverageDocumentSize();
        return result;
    }

    private void waitForThreads(List<Thread> worker) {
        for (Thread t : worker) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public InvertedIndex indexString(DocumentRepository documentRepository, String document, String content) {
        InvertedIndex index = new InvertedIndex(documentRepository);
        RawDocument rawDocument = new RawDocument();
        rawDocument.docNo = document;
        rawDocument.words = Preprocessor.getInstance().preprocess(content);
        return constructIndex(documentRepository, index, rawDocument);
    }

    public InvertedIndex indexFile(DocumentRepository documentRepository, File file) {
        if (!file.exists()) {
            throw new RuntimeException("File to index does not exist");
        }

        try (FileInputStream fstream = new FileInputStream(file)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Charset.forName("ISO8859-1")));

            String strLine;
            RawDocument currentDocument = null;
            StringBuilder content =  null;
            InvertedIndex index = new InvertedIndex(documentRepository);
            while ((strLine = br.readLine()) != null)   {
                int currentPos = 0;
                while (true) {
                    int startPos = strLine.indexOf('<', currentPos);
                    if (startPos == -1) {
                        break;
                    }
                    int endPos = strLine.indexOf('>', startPos);
                    if (content != null) {
                        content.append(strLine, currentPos, startPos);
                    }

                    String text = strLine.substring(startPos + 1, endPos).toUpperCase();
                    if (text.equals("DOCNO")) {
                        content = new StringBuilder();
                    } else if (text.equals("DOC")) {
                        currentDocument = new RawDocument();
                    } else if (text.equals("TEXT")) {
                        content = new StringBuilder();
                    } else if (text.equals("/DOCNO")) {
                        currentDocument.docNo = content.toString().replaceAll("\"", "").trim();
                        content = null;
                    } else if (text.equals("/TEXT")) {
                        currentDocument.words = Preprocessor.getInstance().preprocess(content.toString());
                        content = null;
                    } else if (text.equals("/DOC")) {
                        if (currentDocument.words != null && currentDocument.docNo != null && currentDocument.words.size() > 0) {
                            this.constructIndex(documentRepository, index, currentDocument);
                        }
                        currentDocument = null;
                    }

                    currentPos = endPos + 1;
                }
                if (content != null) {
                    content.append(strLine, currentPos, strLine.length());
                    content.append('\n');
                }
            }
            br.close();
            return index;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private InvertedIndex constructIndex(DocumentRepository documentRepository, InvertedIndex index, RawDocument doc) {
        // build inverted index for file
        // register documents to repository
        DocumentInfo info = documentRepository.register(doc.docNo, doc.words.size());

        // make index
        for (String word : doc.words) {
            IndexValue val = index.putWord(word, info.getId(), 1);
            int currentFrequency = val.getFrequencyInDocument(info.getId());
            if (currentFrequency > info.getMaxFrequencyOfWord()) {
                info.setMaxFrequencyOfWord(currentFrequency);
            }
        }
        return index;
    }


}
