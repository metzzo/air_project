package score;

import indexer.DocumentInfo;
import indexer.InvertedIndex;

import java.util.List;

public interface Scorer {
    double scoreDocumentByQuery(InvertedIndex index, DocumentInfo documentInfo, String word);
}
