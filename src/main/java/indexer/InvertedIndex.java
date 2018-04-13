package indexer;

import java.util.*;

public class InvertedIndex {
    public static class IndexValue {
        Set<String> documents;

        public IndexValue(Set<String> documents) {
            this.documents = documents;
        }
        public IndexValue(String document) {
            this.documents = new HashSet<>(Arrays.asList(document));
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
            return other.documents.containsAll(this.documents) && this.documents.containsAll(other.documents);
        }
    }

    private Map<String, IndexValue> index;

    public InvertedIndex() {
        this.index = new HashMap<>();
    }

    public void putWord(String word, String document) {
        if (!this.index.containsKey(word)) {
            this.index.put(word, new IndexValue(document));
        } else {
            IndexValue val = this.index.get(word);
            val.documents.add(document);
        }
    }

    public void merge(InvertedIndex other) {
        for (String word : other.index.keySet()) {
            IndexValue value = other.index.get(word);
            for (String doc : value.documents) {
                this.putWord(word, doc);
            }
        }
    }

    public Map<String, IndexValue> getIndex() {
        return this.index;
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
}
