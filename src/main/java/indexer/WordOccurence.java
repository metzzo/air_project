package indexer;

public class WordOccurence {
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
