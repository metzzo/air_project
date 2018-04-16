import indexer.DocumentInfo;
import indexer.DocumentRepository;
import indexer.InvertedIndex;
import indexer.WordOccurence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import score.ScoreCalculator;

import score.SimilarityCalculator;
import search.SearchResult;
import search.Searcher;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class SearcherTest {
    private DocumentRepository repo;

    @BeforeEach
    void setUp() {
        repo = new DocumentRepository();
    }

    @Test
    void simpleSearchWorking() {
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



        SimilarityCalculator similarity = (index1, queryIndex, documentInfo, queryDoc, scorer) -> {
            if (documentInfo.getId() == doc1.getId()) {
                return 3;
            } else if (documentInfo.getId() == doc2.getId()) {
                return 2;
            } else {
                return 1;
            }
        };

        // act
        List<SearchResult> result = Searcher.getInstance().search(index, "hallo robert wat", null, similarity, 2);

        // assert
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getDocumentId(), is(doc1.getId()));
        assertThat(result.get(0).getScore(), is(3.0));
        assertThat(result.get(0).getRank(), is(0));
        assertThat(result.get(1).getDocumentId(), is(doc2.getId()));
        assertThat(result.get(1).getScore(), is(2.0));
        assertThat(result.get(1).getRank(), is(1));
    }
}