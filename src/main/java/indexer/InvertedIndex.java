package indexer;

import java.io.*;
import java.util.*;

public class InvertedIndex {
    private final DocumentRepository documentRepo;
    private Map<String, IndexValue> index;

    public InvertedIndex(DocumentRepository repo) {
        this.index = new HashMap<>();
        this.documentRepo = repo;
    }
    public InvertedIndex(DocumentRepository repo, int expectedSize) {
        this.index = new HashMap<>(expectedSize);
        this.documentRepo = repo;
    }

    public IndexValue putWord(String word, WordOccurence wordOccurence) {
        return this.putWord(word, wordOccurence.document, wordOccurence.count);
    }

    public IndexValue putWord(String word, int document, int count) {
        IndexValue val;
        if (!this.index.containsKey(word)) {
            val = new IndexValue(document, count);
            this.index.put(word, val);
        } else {
            val = this.index.get(word);
            val.putWord(document, count);
        }
        return val;
    }

    public void merge(InvertedIndex other) {
        this.documentRepo.merge(other.documentRepo);

        for (String word : other.index.keySet()) {
            IndexValue value = other.index.get(word);
            Map<Integer, Integer> documents = value.getFrequenciesForDocuments();
            for (Integer document : documents.keySet()) {
                int count = documents.get(document);
                this.putWord(word, document, count);
            }
        }
    }

    public IndexValue findByWord(String word) {
        return this.index.get(word);
    }

    public boolean containsWord(String word) {
        return this.index.containsKey(word);
    }

    public int getNumWords() {
        return this.index.size();
    }

    public void debugPrint() {
        for (String word : this.index.keySet()) {
            IndexValue value = this.index.get(word);
            Map<Integer, Integer> documents = value.getFrequenciesForDocuments();
            System.out.print(word + ": ");
            for (Integer document : documents.keySet()) {
                int count = documents.get(document);
                System.out.print(document + "(" + count + ");");
            }
            System.out.println();
        }
        System.out.println();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InvertedIndex) || getClass() != obj.getClass()) {
            return false;
        }
        InvertedIndex other = (InvertedIndex)obj;

        if (!this.documentRepo.equals(other.documentRepo)) {
            return false;
        }

        for (String word : this.index.keySet()) {
            IndexValue myValue = this.index.get(word);
            IndexValue otherValue = other.index.get(word);
            if (!myValue.equals(otherValue)) {
                return false;
            }
        }

        for (String word : other.index.keySet()) {
            IndexValue myValue = this.index.get(word);
            IndexValue otherValue = other.index.get(word);
            if (!myValue.equals(otherValue)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (String word : this.index.keySet()) {
            hash += word.hashCode() + this.index.get(word).hashCode();
        }
        return hash;
    }

    public void serialize(OutputStream stream) {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(stream));
        try {
            dos.writeInt(42);
            dos.writeInt(this.index.size());
            for (String word : this.index.keySet()) {
                dos.writeUTF(word);

                IndexValue val = this.index.get(word);
                val.serialize(dos);
            }
            dos.writeInt(42);
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static InvertedIndex deserialize(DocumentRepository documentRepository, InputStream stream) {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(stream));
        try {
            if (dis.readInt() != 42) {
                throw new RuntimeException("Unexpected format");
            }

            int numOfWords = dis.readInt();
            InvertedIndex index = new InvertedIndex(documentRepository, numOfWords);
            System.out.println("Deserializing InvertedIndex");
            double lastpercent = 0;
            for (int i = 0; i < numOfWords; i++) {
                String word = dis.readUTF();
                IndexValue val = IndexValue.deserialize(dis);
                index.index.put(word, val);

                double percent = i / (double)numOfWords;
                if (percent >= lastpercent + 0.1) {
                    System.out.println((int)(percent*100) + "%");
                    lastpercent = percent;
                }
            }

            if (dis.readInt() != 42) {
                throw new RuntimeException("Unexpected format");
            }
            dis.close();

            return index;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DocumentRepository getDocumentRepository() {
        return this.documentRepo;
    }

    public Set<String> getWords() {
        return this.index.keySet();
    }

    public void checkIndex() {
        for (String word : getWords()) {
            IndexValue val = findByWord(word);
            for (Integer doc : val.getAllDocuments()) {
                if (getDocumentRepository().getDocumentById(doc) == null) {
                    throw new RuntimeException("Index invalid");
                }
            }
        }
    }
}
