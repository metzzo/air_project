package score;

import indexer.DocumentInfo;
import indexer.InvertedIndex;

import java.util.List;

public interface ScoreCalculator {
    double scoreOfQuery(InvertedIndex index, InvertedIndex queryIndex, DocumentInfo documentInfo, DocumentInfo queryDoc, ScoreFunction scorer, List<String> terms, boolean penalize);
}
