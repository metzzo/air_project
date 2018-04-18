package indexer;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class IndexValue {
    private Map<Integer, Integer> documents = new HashMap<>();

    public IndexValue(Set<WordOccurence> documents) {
        for (WordOccurence occurence : documents) {
            this.putWord(occurence);
        }
    }

    public IndexValue(int expectedSize) {
        this(new HashSet<>(expectedSize));
    }

    public IndexValue(WordOccurence occurence) {
        this.putWord(occurence);
    }

    public IndexValue(int document, int count) {
        this.putWord(document, count);
    }


    public int getFrequencyInDocument(int document) {
        return this.documents.get(document);
    }

    public boolean isInDocument(int document) {
        return this.documents.containsKey(document);
    }

    public int getNumDocuments() {
        return this.documents.size();
    }

    void putWord(WordOccurence occurence) {
        this.putWord(occurence.document, occurence.count);
    }

    public void putWord(int document, int count) {
        if (this.documents.containsKey(document)) {
            int oldCount = this.documents.get(document);
            this.documents.put(document, oldCount + count);
        } else {
            this.documents.put(document, count);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IndexValue) || getClass() != obj.getClass()) {
            return false;
        }
        IndexValue other = (IndexValue)obj;
        return other.documents.equals(this.documents);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (Integer doc : documents.keySet()) {
            hash = doc * documents.size() + documents.get(doc).hashCode();
        }
        return hash;
    }

    public Set<Integer> getAllDocuments() {
        return this.documents.keySet();
    }

    public Map<Integer, Integer> getFrequenciesForDocuments() {
        return this.documents;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeInt(documents.size());
        for (Integer doc : this.documents.keySet()) {
            Integer frequency = this.documents.get(doc);
            dos.writeInt(doc);
            dos.writeInt(frequency);
        }
    }
    public static IndexValue deserialize(DataInputStream dis) throws IOException  {
        int count = dis.readInt();
        IndexValue val = new IndexValue(count);
        for (int i = 0; i < count; i++) {
            int doc = dis.readInt();
            int frequency = dis.readInt();
            val.documents.put(doc, frequency);
        }
        return val;
    }

}
