import indexer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import score.CosineScore;
import score.ScoreFunction;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class SimilarityTest {
    private DocumentRepository repo;

    @BeforeEach
    void setUp() {
        repo = new DocumentRepository();
    }

    @Test
    void cosineSimilarityWorking() {
        // arrange
        DocumentInfo doc1 = repo.register("doc1", 0);
        DocumentInfo doc2 = repo.register("doc2", 0);
        DocumentInfo doc3 = repo.register("doc3", 0);

        InvertedIndex index = new InvertedIndex(repo);
        index.putWord("hallo", new WordOccurence(doc1.getId(), 4));
        index.putWord("robert", new WordOccurence(doc1.getId(), 2));
        index.putWord("wat", new WordOccurence(doc1.getId(), 1));


        index.putWord("hallo", new WordOccurence(doc2.getId(), 8));
        index.putWord("robert", new WordOccurence(doc2.getId(), 1));
        index.putWord("wat", new WordOccurence(doc2.getId(), 1));


        index.putWord("hallo", new WordOccurence(doc3.getId(), 1));
        index.putWord("robert", new WordOccurence(doc3.getId(), 2));
        index.putWord("wat", new WordOccurence(doc3.getId(), 4));

        ScoreFunction score = new ScoreFunction() {
            @Override
            public double scoreWord(InvertedIndex calcIndex, InvertedIndex queryIndex, DocumentInfo documentInfo, String word) {
                if (word.equals("hallo")) {
                    if (calcIndex == index) {
                        return 0.1;
                    } else {
                        return 0.2;
                    }
                } else if (word.equals("robert")) {
                    if (calcIndex == index) {
                        return 0.3;
                    } else {
                        return 0.4;
                    }
                } else {
                    if (calcIndex == index) {
                        return 0.5;
                    } else {
                        return 0.6;
                    }
                }
            }
        };

        DocumentRepository queryRepo = new DocumentRepository();
        DocumentInfo queryDoc = queryRepo.register("query", 10);
        InvertedIndex queryIndex = new InvertedIndex(queryRepo);
        queryIndex.putWord("hallo", new WordOccurence(queryDoc.getId(), 1));
        queryIndex.putWord("robert", new WordOccurence(queryDoc.getId(), 1));


        double score_word1_doc = score.scoreWord(index, index, doc1, "hallo");
        double score_word2_doc = score.scoreWord(index, index, doc1, "robert");
        double score_word1_query = score.scoreWord(index, queryIndex, queryDoc, "hallo");
        double score_word2_query = score.scoreWord(index, queryIndex, queryDoc, "robert");

        double length_doc = Math.sqrt(score_word1_doc*score_word1_doc + score_word2_doc*score_word2_doc);
        double length_query = Math.sqrt(score_word1_query*score_word1_query + score_word2_query*score_word2_query);

        double expectedSimilarity = (score_word1_doc * score_word1_query + score_word2_doc * score_word2_query) / (length_doc * length_query);

        // act
        double similarity = new CosineScore().scoreOfQuery(index, queryIndex, doc1, queryDoc, score, Arrays.asList("hallo", "robert"), false);

        // assert
        assertThat(similarity, is(greaterThan(0.1)));
        assertThat(similarity, is(expectedSimilarity));
    }
}
