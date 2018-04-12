package indexer;

import java.util.*;

public class InvertedIndex {
    public static class IndexValue {
        Set<String> documents;

        IndexValue(Set<String> documents) {
            this.documents = documents;
        }
        IndexValue(String document) {
            this.documents = new HashSet<>(Arrays.asList(document));
        }
    }

    private Map<String, IndexValue> index;

    public InvertedIndex() {
        this.index = new HashMap<>();
    }

    public void putWord(String word, String document) {
        if (!this.index.containsKey(word)) {
            this.index.put(word, new IndexValue(word));
        } else {
            IndexValue val = this.index.get(word);
            val.documents.add(document);
        }
    }

    public void merge(InvertedIndex other) {
        for (String word : this.index.keySet()) {
            IndexValue value = this.index.get(word);
            for (String doc : value.documents) {
                this.putWord(word, doc);
            }
        }
    }
}
