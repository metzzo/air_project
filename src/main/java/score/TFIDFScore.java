package score;

import indexer.DocumentInfo;
import indexer.InvertedIndex;


public class TFIDFScore implements Scorer {
    public double scoreDocumentByQuery(InvertedIndex index, DocumentInfo documentInfo, String word) {
        double tf = ScoreUtility.getInstance().tf(index, documentInfo, word);
        double idf = ScoreUtility.getInstance().idf(index, word);
        return tf * idf;
    }
}
