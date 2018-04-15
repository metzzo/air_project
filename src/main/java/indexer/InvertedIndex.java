package indexer;

import java.util.*;

public class InvertedIndex {

    private Map<String, IndexValue> index;
    private Set<String> documents;

    public InvertedIndex() {
        this.index = new HashMap<>();
        this.documents = new HashSet<>();
    }

    public void putWord(String word, WordOccurence wordOccurence) {
        this.documents.add(wordOccurence.document);

        if (!this.index.containsKey(word)) {
            this.index.put(word, new IndexValue(wordOccurence));
        } else {
            IndexValue val = this.index.get(word);
            val.putWord(wordOccurence);
        }
    }

    public void merge(InvertedIndex other) {
        for (String word : other.index.keySet()) {
            IndexValue value = other.index.get(word);
            for (String document : value.documents.keySet()) {
                int count = value.documents.get(document);
                this.putWord(word, new WordOccurence(document, count));
            }
        }
    }

    public Map<String, IndexValue> getIndex() {
        return this.index;
    }

    public int getNumDocuments() {
        return this.documents.size();
    }

    public void debugPrint() {
        for (String word : this.index.keySet()) {
            IndexValue value = this.index.get(word);
            System.out.print(word + ": ");
            for (String document : value.documents.keySet()) {
                int count = value.documents.get(document);
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
}
