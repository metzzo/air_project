package score;

import indexer.DocumentInfo;
import indexer.InvertedIndex;

import java.util.List;

public interface ScoreCalculator {
    double scoreWord(InvertedIndex index, DocumentInfo documentInfo, String word);
}
