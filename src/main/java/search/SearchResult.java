package search;

import indexer.DocumentRepository;

public class SearchResult implements Comparable<SearchResult> {
    private int document;
    private double score;
    private int rank;

    public SearchResult(int document, double score) {
        this.document = document;
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public int getDocumentId() {
        return this.document;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public int compareTo(SearchResult o) {
        return Double.compare(o.score, this.score);
    }
}
