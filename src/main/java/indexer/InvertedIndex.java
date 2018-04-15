package indexer;

import java.util.*;

public class InvertedIndex {

    private Map<String, IndexValue> index;
    private Map<String, Integer> maxWordsPerDocument;

    public InvertedIndex() {
        this.index = new HashMap<>();
        this.maxWordsPerDocument = new HashMap<>();
    }

    public void putWord(String word, WordOccurence wordOccurence) {
        if (!this.index.containsKey(word)) {
            this.index.put(word, new IndexValue(wordOccurence));
        } else {
            IndexValue val = this.index.get(word);
            val.putWord(wordOccurence);
        }

        if (!this.maxWordsPerDocument.containsKey(wordOccurence.document)) {
            this.maxWordsPerDocument.put(wordOccurence.document, 0);
        }

        IndexValue val = this.index.get(word);
        int count = val.getFrequencyInDocument(wordOccurence.document);
        int currentMaxCount = this.maxWordsPerDocument.get(wordOccurence.document);
        if (count > currentMaxCount) {
            this.maxWordsPerDocument.put(wordOccurence.document, count);
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

    public IndexValue findByWord(String word) {
        return this.index.get(word);
    }

    public boolean containsWord(String word) {
        return this.index.containsKey(word);
    }

    public int getMaxFrequencyOfWord(String document) {
        return this.maxWordsPerDocument.get(document);
    }

    public int getNumDocuments() {
        return this.maxWordsPerDocument.size();
    }

    public int getNumWords() {
        return this.index.size();
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
