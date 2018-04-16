package score;

import indexer.DocumentInfo;
import indexer.InvertedIndex;

public interface SimilarityCalculator {
    double similarityToQuery(InvertedIndex index, InvertedIndex queryIndex, DocumentInfo documentInfo, DocumentInfo queryDoc, ScoreCalculator scorer);
}
