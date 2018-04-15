package score;

import indexer.DocumentRepository;
import indexer.InvertedIndex;

import java.util.List;

public class BM25Score implements Scorer {

    private final double k1;
    private final double b;

    public BM25Score(double k1, double b) {
        this.k1 = k1;
        this.b = b;
    }

    @Override
    public double scoreDocumentByQuery(InvertedIndex index, int document, List<String> query) {
        double score = 0.0;
        for (String word : query) {
            // check if word is in index
            if (!index.containsWord(word)) {
                continue;
            }
            // check if word is in document
            if (!index.findByWord(word).isInDocument(document)) {
                continue;
            }

            double idf = ScoreUtility.getInstance().idf(index, word);
            double tf = ScoreUtility.getInstance().tf(index, document, word);
            double Ld = DocumentRepository.getInstance().getDocumentById(document).getSize();
            double Lave = DocumentRepository.getInstance().getAverageDocumentSize();
            double nominator = (this.k1 + 1.0)*tf;
            double denominator = this.k1 * ((1 - this.b) + this.b * (Ld / Lave)) + tf;

            score += idf * (nominator / denominator);
        }

        return score;
    }
}
