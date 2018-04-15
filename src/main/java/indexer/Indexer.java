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
            } while (index1 != null && index2 != null);
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
                    InvertedIndex newIndex = Indexer.getInstance().indexFile(toProcess);
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

        System.out.println("Reduce...");
        // reduce
        int numIndices = indices.size();
        for (int i = 0; i < numThreads; i++) {
            ReduceThread t = new ReduceThread(indices, numIndices);
            t.start();
            worker.add(t);
        }
        waitForThreads(worker);

        return indices.get(0);
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

    public InvertedIndex indexFile(File file) {
        if (!file.exists()) {
            throw new RuntimeException("File to index does not exist");
        }
        List<RawDocument> documents = new LinkedList<>();

        try (FileInputStream fstream = new FileInputStream(file)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Charset.forName("ISO8859-1")));

            String strLine;
            RawDocument currentDocument = null;
            StringBuilder content =  null;
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
                        currentDocument.docNo = content.toString().trim();
                        content = null;
                    } else if (text.equals("/TEXT")) {
                        currentDocument.words = Preprocessor.getInstance().preprocess(content.toString());
                        content = null;
                    } else if (text.equals("/DOC")) {
                        documents.add(currentDocument);
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        // build inverted index for file
        InvertedIndex baseIndex = new InvertedIndex();
        for (RawDocument doc : documents) {
            // register documents to repository
            DocumentInfo info = DocumentRepository.getInstance().register(doc.docNo, doc.words.size());

            // make index
            InvertedIndex index = new InvertedIndex();
            for (String word : doc.words) {
                IndexValue val = index.putWord(word, new WordOccurence(info.getId(), 1));
                int currentFrequency = val.getFrequencyInDocument(info.getId());
                if (currentFrequency > info.getMaxFrequencyOfWord()) {
                    info.setMaxFrequencyOfWord(currentFrequency);
                }
            }

            baseIndex.merge(index);
        }

        return baseIndex;
    }
}
