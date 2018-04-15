package score;

import indexer.InvertedIndex;

public class ScoreUtility {
    private static ScoreUtility ourInstance = new ScoreUtility();

    public static ScoreUtility getInstance() {
        return ourInstance;
    }

    private ScoreUtility() {
    }



    public double idf(InvertedIndex index, String word) {
        double numDocumentsContainingWord = index.findByWord(word).getNumDocuments();
        double numDocuments = (double) index.getNumDocuments();
        return Math.log(numDocuments / numDocumentsContainingWord);
    }
}
