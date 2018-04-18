package score;

import indexer.DocumentInfo;
import indexer.InvertedIndex;

public interface ScoreFunction {
    double scoreWord(InvertedIndex index, InvertedIndex queryIndex, DocumentInfo queryDocument, String word);
}
