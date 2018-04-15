package score;

import indexer.IndexValue;
import indexer.InvertedIndex;

import java.util.List;

public class TFIDFScore implements Scorer {
    private double tf(InvertedIndex index, String document, String word) {
        IndexValue indexValue = index.findByWord(word);
        double frequency = (double) indexValue.getFrequencyInDocument(document);
        double maxFrequency = (double) index.getMaxFrequencyOfWord(document);
        return frequency / maxFrequency;
    }

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

            double tf = this.tf(index, document, word);
            double idf = ScoreUtility.getInstance().idf(index, word);
            score += tf * idf;
        }
        return score;
    }
}