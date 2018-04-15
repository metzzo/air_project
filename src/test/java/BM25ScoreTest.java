import indexer.DocumentInfo;
import indexer.DocumentRepository;
import indexer.InvertedIndex;
import indexer.WordOccurence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import score.BM25Score;
import score.Scorer;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class BM25ScoreTest {
    private InvertedIndex index;
    private Scorer scorer;
    private DocumentInfo doc1;
    private DocumentInfo doc2;
    private DocumentInfo doc3;

    @BeforeEach
    void setUp() {
        DocumentRepository.getInstance().clear();
        this.doc1 = DocumentRepository.getInstance().register("doc1", 7);
        this.doc2 = DocumentRepository.getInstance().register("doc2", 10);
        this.doc3 = DocumentRepository.getInstance().register("doc3", 7);

        this.index = new InvertedIndex();
        this.index.putWord("a", new WordOccurence(doc1.getId(), 4));
        this.index.putWord("b", new WordOccurence(doc1.getId(), 2));
        this.index.putWord("c", new WordOccurence(doc1.getId(), 1));


        this.index.putWord("d", new WordOccurence(doc2.getId(), 8));
        this.index.putWord("e", new WordOccurence(doc2.getId(), 1));
        this.index.putWord("f", new WordOccurence(doc2.getId(), 1));


        this.index.putWord("a", new WordOccurence(doc3.getId(), 1));
        this.index.putWord("b", new WordOccurence(doc3.getId(), 2));
        this.index.putWord("c", new WordOccurence(doc3.getId(), 4));


        this.scorer = new BM25Score(1.5, 0.75);
    }

    @Test
    void scoreWithDistinctSearchTerms() {
        // arrange
        List<String> query = Arrays.asList("x", "y", "z");

        // act
        double score = scorer.scoreDocumentByQuery(this.index, this.doc1.getId(), query);

        // assert
        assertThat(score, is(0.0));
    }

    @Test
    void scoreWithExactMatch() {
        // arrange
        List<String> query = Arrays.asList("d", "e", "f");

        // act
        double score = scorer.scoreDocumentByQuery(this.index, this.doc2.getId(), query);

        // assert
        assertThat(score, is(1.3477174143543214));
    }

    @Test
    void scoreWithPartialMatch() {
        // arrange
        List<String> query = Arrays.asList("a", "x", "y");

        // act
        double score = scorer.scoreDocumentByQuery(this.index, this.doc1.getId(), query);

        // assert
        assertThat(score, is(0.4296319026311676));
    }
}