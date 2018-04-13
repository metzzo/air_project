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
    private static class Document {
        String docNo;
        List<String> words;
    }

    class IndexingThread extends Thread {
        private final List<File> queue;
        private InvertedIndex index;

        IndexingThread(List<File> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            this.index = new InvertedIndex();
            File toProcess;
            do {
                synchronized (this.queue) {
                    if (this.queue.size() > 0) {
                        toProcess = this.queue.get(0);
                        this.queue.remove(0);

                        synchronized (System.out) {
                            System.out.println("Indexing File " + toProcess.getPath() + " remaining files " + this.queue.size());
                        }
                    } else {
                        toProcess = null;
                    }
                }

                if (toProcess != null) {
                    InvertedIndex newIndex = Indexer.getInstance().indexFile(toProcess);
                    this.index.merge(newIndex);
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
        List<IndexingThread> worker = new LinkedList<>();
        List<File> toIndex = new LinkedList<>(files);
        for (int i = 0; i < numThreads; i++) {
            IndexingThread t = new IndexingThread(toIndex);
            t.start();
            worker.add(t);
        }
        System.out.println("Files to index: " + files.size());
        for (Thread t : worker) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        InvertedIndex index = null;
        for (IndexingThread t : worker) {
            if (index == null) {
                index = t.index;
            } else {
                index.merge(t.index);
            }
        }

        return index;
    }
    public InvertedIndex indexFile(File file) {
        if (!file.exists()) {
            throw new RuntimeException("File to index does not exist");
        }
        List<Document> documents = new LinkedList<>();

        try (FileInputStream fstream = new FileInputStream(file)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Charset.forName("ISO8859-1")));

            String strLine;
            Document currentDocument = null;
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
                        currentDocument = new Document();
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
        for (Document doc : documents) {
            InvertedIndex index = new InvertedIndex();
            for (String word : doc.words) {
                index.putWord(word, doc.docNo);
            }

            baseIndex.merge(index);
        }

        return baseIndex;
    }
}
