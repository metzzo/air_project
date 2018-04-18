package score;

import indexer.DocumentInfo;
import indexer.InvertedIndex;

import java.util.List;

public class CosineScore implements ScoreCalculator {
    public double scoreOfQuery(InvertedIndex index, InvertedIndex queryIndex, DocumentInfo documentInfo, DocumentInfo queryDoc, ScoreFunction scorer, List<String> terms, boolean penalize) {
        double[] query_vec = new double[queryIndex.getWords().size()];
        double[] doc_vec = new double[queryIndex.getWords().size()];
        int current_pos = 0;
        for (String word : queryIndex.getWords()) {
            // check if word is in document and check if is in index
            if (index.containsWord(word)) {
                query_vec[current_pos] = scorer.scoreWord(index, queryIndex, queryDoc, word);
                if (index.findByWord(word).isInDocument(documentInfo.getId())) {
                    doc_vec[current_pos] = scorer.scoreWord(index, index, documentInfo, word);
                } else if (penalize) {
                    doc_vec[current_pos] = -query_vec[current_pos]; // thats bad => penalty
                }

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

        double similarity = dot / (length_query_vec * length_doc_vec);

        return similarity;
    }
}
