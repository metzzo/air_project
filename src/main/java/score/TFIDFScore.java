package score;

import indexer.InvertedIndex;

import java.util.List;

public class TFIDFScore implements Scorer {
    public double scoreDocumentByQuery(InvertedIndex index, String document, List<String> query) {
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

            double tf = ScoreUtility.getInstance().tf(index, document, word);
            double idf = ScoreUtility.getInstance().idf(index, word);
            score += tf * idf;
        }
        return score;
    }
}
