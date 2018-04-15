package indexer;

public class WordOccurence {
    int document;
    int count;

    public WordOccurence(int doc) {
        this(doc, 1);
    }

    public WordOccurence(int doc, int count) {
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

        return this.document == other.document;
    }
}
