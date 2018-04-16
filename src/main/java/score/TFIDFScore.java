package score;

import indexer.DocumentInfo;
import indexer.InvertedIndex;


public class TFIDFScore implements ScoreCalculator {
    public double scoreWord(InvertedIndex index, DocumentInfo documentInfo, String word) {
        double tf = ScoreUtility.getInstance().tf(index, documentInfo, word);
        double idf = ScoreUtility.getInstance().idf(index, word);
        return tf * idf;
    }
}
