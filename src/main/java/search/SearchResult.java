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

    public String getDocumentName() {
        return DocumentRepository.getInstance().getDocumentById(document).getName();
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public int compareTo(SearchResult o) {
        double delta = o.score - this.score;
        if (delta == 0.0) {
            return 0;
        } else if (delta > 0.0) {
            return 1;
        } else {
            return -1;
        }
    }
}
