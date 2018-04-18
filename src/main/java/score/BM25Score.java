package score;

import indexer.DocumentInfo;
import indexer.InvertedIndex;

public class BM25Score implements ScoreFunction {

    private final double k1;
    private final double b;

    public BM25Score(double k1, double b) {
        this.k1 = k1;
        this.b = b;
    }

    @Override
    public double scoreWord(InvertedIndex index, InvertedIndex queryIndex, DocumentInfo documentInfo, String word) {
        double tf = ScoreUtility.getInstance().tf(queryIndex, documentInfo, word);
        double idf = ScoreUtility.getInstance().idf(index, word);
        double Ld = documentInfo.getSize();
        double Lave = index.getDocumentRepository().getAverageDocumentSize();
        double nominator = (this.k1 + 1.0)*tf;
        double denominator = this.k1 * ((1 - this.b) + this.b * (Ld / Lave)) + tf;

        return idf * (nominator / denominator);
    }
}
