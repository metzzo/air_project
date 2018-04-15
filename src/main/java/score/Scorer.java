package score;

import indexer.InvertedIndex;

import java.util.List;

public interface Scorer {
    double scoreDocumentByQuery(InvertedIndex index, String document, List<String> query);
}
