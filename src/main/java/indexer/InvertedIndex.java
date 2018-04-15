package indexer;

import java.util.*;

public class InvertedIndex {
    public static class WordOccurence {
        String document;
        int count;

        public WordOccurence(String doc) {
            this(doc, 1);
        }

        public WordOccurence(String doc, int count) {
            this.document = doc;
            this.count = count;
        }

        @Override
        public String toString() {
            return document + "(" + count + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof WordOccurence) || getClass() != obj.getClass()) {
                return false;
            }
            WordOccurence other = (WordOccurence)obj;

            return this.document.equals(other.document);
        }
    }

    public static class IndexValue {
        Map<String, Integer> documents = new HashMap<>();

        public IndexValue(Set<WordOccurence> documents) {
            for (WordOccurence occurence : documents) {
                this.putWord(occurence);
            }
        }

        public IndexValue(WordOccurence occurence) {
            this.putWord(occurence);
        }

        public Map<String, Integer> getDocuments() {
            return this.documents;
        }

        private void putWord(WordOccurence occurence) {
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
    }

    private Map<String, IndexValue> index;

    public InvertedIndex() {
        this.index = new HashMap<>();
    }

    public void putWord(String word, WordOccurence wordOccurence) {
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
