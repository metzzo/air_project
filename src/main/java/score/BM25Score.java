package score;

import indexer.DocumentInfo;
import indexer.DocumentRepository;
import indexer.InvertedIndex;

public class BM25Score implements Scorer {

    private final double k1;
    private final double b;

    public BM25Score(double k1, double b) {
        this.k1 = k1;
        this.b = b;
    }

    @Override
    public double scoreDocumentByQuery(InvertedIndex index, DocumentInfo documentInfo, String word) {
        double idf = ScoreUtility.getInstance().idf(index, word);
        double tf = ScoreUtility.getInstance().tf(index, documentInfo, word);
        double Ld = documentInfo.getSize();
        double Lave = index.getDocumentRepository().getAverageDocumentSize();
        double nominator = (this.k1 + 1.0)*tf;
        double denominator = this.k1 * ((1 - this.b) + this.b * (Ld / Lave)) + tf;

        return idf * (nominator / denominator);
    }
}
