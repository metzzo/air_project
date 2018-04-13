package indexer;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import preprocess.Preprocessor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
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

    class XMLHandler extends DefaultHandler {
        private final List<Document> documents;
        private Document currentDocument;
        private StringBuilder content;

        public XMLHandler(List<Document> documents) {
            this.documents = documents;
        }

        public void startDocument() throws SAXException {
        }

        public void startElement(String namespaceURI,
                                 String localName,
                                 String qName,
                                 Attributes atts)
                throws SAXException {

            switch(localName.toUpperCase()) {
                case "DOC":
                    this.currentDocument = new Document();
                    this.content = new StringBuilder();
                    break;
                case "DOCNO":
                case "TEXT":
                    this.content = new StringBuilder();
                    break;

            }
        }

        public void endElement (String uri, String localName, String qName)
                throws SAXException
        {
            switch(localName.toUpperCase()) {
                case "DOC":
                    this.documents.add(this.currentDocument);
                    this.currentDocument = null;
                    this.content = null;
                    break;
                case "DOCNO":
                    this.currentDocument.docNo = this.content.toString().trim();
                    this.content = null;
                    break;
                case "TEXT":
                    String content = this.content.toString();
                    this.currentDocument.words = Preprocessor.getInstance().preprocess(content);
                    this.content = null;
                    break;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String s =  new String(ch, start, length);
            if (this.content != null) {
                this.content.append(s);
            }
        }

        public void endDocument() throws SAXException {

        }
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
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        try {
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(new XMLHandler(documents));

            // trick parser to think its XML
            List<InputStream> streams = Arrays.asList(
                    new ByteArrayInputStream("<root>".getBytes()),
                    new FileInputStream(file),
                    new ByteArrayInputStream("</root>".getBytes())
            );
            InputStream is = new SequenceInputStream(Collections.enumeration(streams));
            xmlReader.parse(new InputSource(is));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

//        for (Document doc : documents) {
//            System.out.println(doc.docNo);
//            for (String word : doc.words) {
//                System.out.print(word + " ");
//            }
//            System.out.println();
//        }

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
