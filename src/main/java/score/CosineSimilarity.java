package score;

import indexer.DocumentInfo;
import indexer.InvertedIndex;

public class CosineSimilarity implements SimilarityCalculator {
    public double similarityToQuery(InvertedIndex index, InvertedIndex queryIndex, DocumentInfo documentInfo, DocumentInfo queryDoc, ScoreCalculator scorer) {
        double[] query_vec = new double[queryDoc.getSize()];
        double[] doc_vec = new double[queryDoc.getSize()];
        int current_pos = 0;
        for (String word : queryIndex.getWords()) {
            // check if word is in document and check if is in index
            if (index.findByWord(word).isInDocument(documentInfo.getId()) && index.containsWord(word)) {
                doc_vec[current_pos] = scorer.scoreWord(index, documentInfo, word);
                query_vec[current_pos] = scorer.scoreWord(queryIndex, queryDoc, word);
            }
            current_pos++;
        }

        // calculate cosine similarity
        double dot = 0.0;
        double length_query_vec = 0.0;
        double length_doc_vec = 0.0;
        for (int i = 0; i < query_vec.length; i++) {
            dot += query_vec[i] * doc_vec[i];
            length_doc_vec += doc_vec[i] * doc_vec[i];
            length_query_vec += query_vec[i] * query_vec[i];
        }

        length_query_vec = Math.sqrt(length_query_vec);
        length_doc_vec = Math.sqrt(length_doc_vec);

        return dot / (length_query_vec * length_doc_vec);
    }
}
