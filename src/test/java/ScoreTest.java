import indexer.DocumentInfo;
import indexer.DocumentRepository;
import indexer.InvertedIndex;
import indexer.WordOccurence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import score.BM25Score;
import score.BM25VAScore;
import score.ScoreCalculator;
import score.TFIDFScore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ScoreTest {
    private InvertedIndex index;
    private DocumentInfo doc1;
    private DocumentInfo doc2;
    private DocumentInfo doc3;
    private DocumentRepository repo;

    @BeforeEach
    void setUp() {
        repo = new DocumentRepository();

        this.doc1 = repo.register("doc1", 7);
        this.doc1.setAverageTextFrequency((4 + 2 + 1)/3.0);

        this.doc2 = repo.register("doc2", 10);
        this.doc2.setAverageTextFrequency((8 + 1 + 1)/3.0);

        this.doc3 = repo.register("doc3", 7);
        this.doc3.setAverageTextFrequency((4 + 2 + 1)/3.0);

        this.index = new InvertedIndex(repo);
        this.index.putWord("a", new WordOccurence(doc1.getId(), 4));
        this.index.putWord("b", new WordOccurence(doc1.getId(), 2));
        this.index.putWord("c", new WordOccurence(doc1.getId(), 1));


        this.index.putWord("d", new WordOccurence(doc2.getId(), 8));
        this.index.putWord("e", new WordOccurence(doc2.getId(), 1));
        this.index.putWord("f", new WordOccurence(doc2.getId(), 1));


        this.index.putWord("a", new WordOccurence(doc3.getId(), 1));
        this.index.putWord("b", new WordOccurence(doc3.getId(), 2));
        this.index.putWord("c", new WordOccurence(doc3.getId(), 4));

        this.doc1.setMaxFrequencyOfWord(4);
        this.doc2.setMaxFrequencyOfWord(8);
        this.doc3.setMaxFrequencyOfWord(4);

        this.repo.calculateMetrics();
    }

    @Test
    void scoreWithBM25() {
        // arrange
        ScoreCalculator scorer = new BM25Score(1.5, 0.75);

        // act
        double score =  scorer.scoreWord(this.index, this.index, this.doc2, "d") +
                        scorer.scoreWord(this.index, this.index, this.doc2, "e") +
                        scorer.scoreWord(this.index, this.index, this.doc2, "f");

        // assert
        assertThat(score, is(1.3477174143543214));
    }
    @Test
    void scoreWithBM25VA() {
        // arrange
        ScoreCalculator scorer = new BM25VAScore(1.5);

        // act
        double score =  scorer.scoreWord(this.index, this.index, this.doc2, "d") +
                scorer.scoreWord(this.index, this.index, this.doc2, "e") +
                scorer.scoreWord(this.index, this.index, this.doc2, "f");

        // assert
        assertThat(score, is(1.2530911572239556));
    }

    @Test
    void scoreWithTFIDF() {
        // arrange
        ScoreCalculator scorer = new TFIDFScore();

        // act
        double score =
                scorer.scoreWord(this.index, this.index, this.doc2, "d") +
                scorer.scoreWord(this.index, this.index, this.doc2, "e") +
                scorer.scoreWord(this.index, this.index, this.doc2, "f");


        // assert
        assertThat(score, is(1.373265360835137));
    }
}