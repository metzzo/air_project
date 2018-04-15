package indexer;

import java.io.*;
import java.util.*;

public class InvertedIndex {
    private Map<String, IndexValue> index;

    public InvertedIndex() {
        this.index = new HashMap<>();
    }

    public IndexValue putWord(String word, WordOccurence wordOccurence) {
        IndexValue val;
        if (!this.index.containsKey(word)) {
            val = new IndexValue(wordOccurence);
            this.index.put(word, val);
        } else {
            val = this.index.get(word);
            val.putWord(wordOccurence);
        }
        return val;
    }

    public void merge(InvertedIndex other) {
        for (String word : other.index.keySet()) {
            IndexValue value = other.index.get(word);
            Map<Integer, Integer> documents = value.getFrequenciesForDocuments();
            for (Integer document : documents.keySet()) {
                int count = documents.get(document);
                this.putWord(word, new WordOccurence(document, count));
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

    public void serialize(OutputStream stream) {
        DataOutputStream dos = new DataOutputStream(stream);
        try {
            dos.writeInt(42);
            dos.writeInt(this.index.size());
            for (String word : this.index.keySet()) {
                dos.writeUTF(word);

                IndexValue val = this.index.get(word);
                val.serialize(dos);
            }
            dos.writeInt(42);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static InvertedIndex deserialize(InputStream stream) {
        DataInputStream dis = new DataInputStream(stream);
        try {
            if (dis.readInt() != 42) {
                throw new RuntimeException("Unexpected format");
            }

            InvertedIndex index = new InvertedIndex();
            int numOfWords = dis.readInt();

            for (int i = 0; i < numOfWords; i++) {
                String word = dis.readUTF();
                IndexValue val = IndexValue.deserialize(dis);
                index.index.put(word, val);
            }

            if (dis.readInt() != 42) {
                throw new RuntimeException("Unexpected format");
            }

            return index;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
