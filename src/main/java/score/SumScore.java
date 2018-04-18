package score;

import indexer.DocumentInfo;
import indexer.InvertedIndex;

import java.util.List;

public class SumScore implements ScoreCalculator {

    @Override
    public double scoreOfQuery(InvertedIndex index, InvertedIndex queryIndex, DocumentInfo documentInfo, DocumentInfo queryDoc, ScoreFunction scorer, List<String> terms, boolean penalize) {
        double score = 0.0;
        for (String word : terms) {
            // check if word is in document and check if is in index
            if (index.containsWord(word) && index.findByWord(word).isInDocument(documentInfo.getId())) {
                score += scorer.scoreWord(index, index, documentInfo, word);
            }
        }
        return score;
    }
}
