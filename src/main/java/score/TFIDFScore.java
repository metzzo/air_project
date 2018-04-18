package score;

import indexer.DocumentInfo;
import indexer.InvertedIndex;


public class TFIDFScore implements ScoreFunction {
    public double scoreWord(InvertedIndex index, InvertedIndex queryIndex, DocumentInfo documentInfo, String word) {
        double tf = ScoreUtility.getInstance().tf(queryIndex, documentInfo, word);
        double idf = ScoreUtility.getInstance().idf(index, word);
        return tf * idf;
    }
}
