package indexer;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
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
                    index1.checkIndex();
                    index2.checkIndex();
                    index1.merge(index2);
                    index1.checkIndex();
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
        private StanfordCoreNLP pipeline;

        MapThread(List<File> queue, List<InvertedIndex> indices, int totalNum) {
            this.queue = queue;
            this.indices = indices;
            this.totalNum = totalNum;
            this.pipeline = new StanfordCoreNLP(Preprocessor.stanfordNlpProperties());
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
                    InvertedIndex newIndex = Indexer.getInstance().indexFile(this.pipeline, new DocumentRepository(), toProcess);
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

        if (toIndex.size() > 0) {
            throw new RuntimeException("Why not all indexed?");
        }

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
        if (indices.size() != 1) {
            throw new RuntimeException("Some indices left");
        }
        System.out.println("Calculate Metrics ...");
        result.getDocumentRepository().calculateMetrics();
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

    public InvertedIndex indexString(StanfordCoreNLP pipeline, DocumentRepository documentRepository, String document, String content) {
        InvertedIndex index = new InvertedIndex(documentRepository);
        RawDocument rawDocument = new RawDocument();
        rawDocument.docNo = document;
        rawDocument.words = Preprocessor.getInstance().preprocess(pipeline, content);
        return constructIndex(documentRepository, index, rawDocument);
    }

    public InvertedIndex indexFile(StanfordCoreNLP pipeline, DocumentRepository documentRepository, File file) {
        if (!file.exists()) {
            throw new RuntimeException("File to index does not exist");
        }

        try (FileInputStream fstream = new FileInputStream(file)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Charset.forName("ISO8859-1")));

            String strLine;
            RawDocument currentDocument = null;
            StringBuilder content =  new StringBuilder();;
            InvertedIndex index = new InvertedIndex(documentRepository);
            boolean shouldWrite = false;
            while ((strLine = br.readLine()) != null)   {
                int currentPos = 0;
                while (true) {
                    int startPos = strLine.indexOf('<', currentPos);
                    if (startPos == -1) {
                        break;
                    }
                    int endPos = strLine.indexOf('>', startPos);
                    if (shouldWrite) {
                        content.append(strLine, currentPos, startPos);
                    }

                    String text = strLine.substring(startPos + 1, endPos).toUpperCase();
                    if (text.equals("DOCNO")) {
                        content.delete(0, content.length());
                        shouldWrite = true;
                    } else if (text.equals("DOC")) {
                        currentDocument = new RawDocument();
                    } else if (text.equals("TEXT")) {
                        content.delete(0, content.length());
                        shouldWrite = true;
                    } else if (text.equals("/DOCNO")) {
                        currentDocument.docNo = content.toString().replaceAll("\"", "").trim();
                        shouldWrite = false;
                    } else if (text.equals("/TEXT")) {
                        currentDocument.words = Preprocessor.getInstance().preprocess(pipeline, content.toString());
                        shouldWrite = false;
                    } else if (text.equals("/DOC")) {
                        if (currentDocument.words != null && currentDocument.docNo != null && currentDocument.words.size() > 0) {
                            this.constructIndex(documentRepository, index, currentDocument);
                        }
                        currentDocument = null;
                    }

                    currentPos = endPos + 1;
                }
                if (shouldWrite) {
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
        Set<String> wordsOfDocument = new HashSet<>();
        for (String word : doc.words) {
            wordsOfDocument.add(word);

            IndexValue val = index.putWord(word, info.getId(), 1);
            int currentFrequency = val.getFrequencyInDocument(info.getId());
            if (currentFrequency > info.getMaxFrequencyOfTerm()) {
                info.setMaxFrequencyOfTerm(currentFrequency);
            }
        }

        info.setAverageTermFrequency((double)info.getSize() / (double)wordsOfDocument.size());
        index.checkIndex();
        return index;
    }


}
