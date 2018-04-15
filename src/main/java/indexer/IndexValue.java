package indexer;

import java.util.*;

public class IndexValue {
    private Map<Integer, Integer> documents = new HashMap<>();

    public IndexValue(Set<WordOccurence> documents) {
        for (WordOccurence occurence : documents) {
            this.putWord(occurence);
        }
    }

    public IndexValue() {
        this(new HashSet<>());
    }

    public IndexValue(WordOccurence occurence) {
        this.putWord(occurence);
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
        if (this.documents.containsKey(occurence.document)) {
            int oldCount = this.documents.get(occurence.document);
            this.documents.put(occurence.document, oldCount + occurence.count);
        } else {
            this.documents.put(occurence.document, occurence.count);
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

    public Set<Integer> getAllDocuments() {
        return this.documents.keySet();
    }

    public Map<Integer, Integer> getFrequenciesForDocuments() {
        return this.documents;
    }
}
