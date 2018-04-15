import indexer.DocumentRepository;
import indexer.InvertedIndex;
import indexer.WordOccurence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import score.BM25Score;
import score.Scorer;
import score.TFIDFScore;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class BM25ScoreTest {
    private InvertedIndex index;
    private Scorer scorer;

    @BeforeEach
    void setUp() {
        this.index = new InvertedIndex();
        this.index.putWord("a", new WordOccurence("doc1", 4));
        this.index.putWord("b", new WordOccurence("doc1", 2));
        this.index.putWord("c", new WordOccurence("doc1", 1));


        this.index.putWord("d", new WordOccurence("doc2", 8));
        this.index.putWord("e", new WordOccurence("doc2", 1));
        this.index.putWord("f", new WordOccurence("doc2", 1));


        this.index.putWord("a", new WordOccurence("doc3", 1));
        this.index.putWord("b", new WordOccurence("doc3", 2));
        this.index.putWord("c", new WordOccurence("doc3", 4));

        DocumentRepository.getInstance().clear();
        DocumentRepository.getInstance().register("doc1", 7);
        DocumentRepository.getInstance().register("doc2", 10);
        DocumentRepository.getInstance().register("doc3", 7);

        this.scorer = new BM25Score(1.5, 0.75);
    }

    @Test
    void scoreWithDistinctSearchTerms() {
        // arrange
        List<String> query = Arrays.asList("x", "y", "z");

        // act
        double score = scorer.scoreDocumentByQuery(this.index, "doc1", query);

        // assert
        assertThat(score, is(0.0));
    }

    @Test
    void scoreWithExactMatch() {
        // arrange
        List<String> query = Arrays.asList("d", "e", "f");

        // act
        double score = scorer.scoreDocumentByQuery(this.index, "doc2", query);

        // assert
        assertThat(score, is(1.3477174143543214));
    }

    @Test
    void scoreWithPartialMatch() {
        // arrange
        List<String> query = Arrays.asList("a", "x", "y");

        // act
        double score = scorer.scoreDocumentByQuery(this.index, "doc1", query);

        // assert
        assertThat(score, is(0.4296319026311676));
    }
}