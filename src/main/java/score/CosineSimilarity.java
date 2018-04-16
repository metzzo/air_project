package score;

import indexer.DocumentInfo;
import indexer.DocumentRepository;
import indexer.Indexer;
import indexer.InvertedIndex;

import java.util.List;

public class CosineSimilarity {
    private static CosineSimilarity instance = new CosineSimilarity();
    public static CosineSimilarity getInstance() {
        return instance;
    }
    private CosineSimilarity() { }

    public double scoreDocumentByQuery(InvertedIndex index, DocumentInfo documentInfo, List<String> query, Scorer scorer) {
        // create document out of query

        double score = 0.0;
        double[] query_vec = new double[query.size()];
        double[] doc_vec = new double[query.size()];
        int current_pos = 0;
        for (String word : query) {
            // check if word is in document and check if is in index
            if (index.findByWord(word).isInDocument(documentInfo.getId()) && index.containsWord(word)) {
                doc_vec[current_pos] = scorer.scoreDocumentByQuery(index, documentInfo, word);
                query_vec[current_pos] = scorer.scoreDocumentByQuery(index, documentInfo, word);
            }
            current_pos++;
        }

        return score;
    }
}
