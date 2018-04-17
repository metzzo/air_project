package score;

import indexer.DocumentInfo;
import indexer.InvertedIndex;

public class BM25VAScore implements ScoreCalculator {
    private final double k1;

    public BM25VAScore(double k1) {
        this.k1 = k1;
    }

    @Override
    public double scoreWord(InvertedIndex index, InvertedIndex queryIndex, DocumentInfo documentInfo, String word) {
        double b = this.getB(index, documentInfo);
        return new BM25Score(this.k1, b).scoreWord(index, queryIndex, documentInfo, word);
    }

    private double getB(InvertedIndex index, DocumentInfo documentInfo) {
        double mavgtf = index.getDocumentRepository().getMeanAverageTextFrequency();
        double avgtf = documentInfo.getAverageTextFrequency();
        double avgdl = index.getDocumentRepository().getAverageDocumentSize();
        double Ld = documentInfo.getSize();

        return 1.0 / (mavgtf * mavgtf) * avgtf + (1.0 - (1.0 / mavgtf)) * (Ld / avgdl);
    }
}