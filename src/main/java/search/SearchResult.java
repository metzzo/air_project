package search;

public class SearchResult implements Comparable<SearchResult> {
    private String document;
    private double score;
    private int rank;

    public SearchResult(String document, double score) {
        this.document = document;
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public String getDocument() {
        return document;
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
