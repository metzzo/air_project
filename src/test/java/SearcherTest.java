import indexer.DocumentInfo;
import indexer.DocumentRepository;
import indexer.InvertedIndex;
import indexer.WordOccurence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import score.Scorer;

import search.SearchResult;
import search.Searcher;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class SearcherTest {
    @BeforeEach
    void setUp() {
        DocumentRepository.getInstance().clear();
    }

    @Test
    void simpleSearchWorking() {
        // arrange

        DocumentInfo doc1 = DocumentRepository.getInstance().register("doc1", 0);
        DocumentInfo doc2 = DocumentRepository.getInstance().register("doc2", 0);
        DocumentInfo doc3 = DocumentRepository.getInstance().register("doc3", 0);

        InvertedIndex index = new InvertedIndex();
        index.putWord("a", new WordOccurence(doc1.getId(), 4));
        index.putWord("b", new WordOccurence(doc1.getId(), 2));
        index.putWord("c", new WordOccurence(doc1.getId(), 1));


        index.putWord("a", new WordOccurence(doc2.getId(), 8));
        index.putWord("b", new WordOccurence(doc2.getId(), 1));
        index.putWord("c", new WordOccurence(doc2.getId(), 1));


        index.putWord("a", new WordOccurence(doc3.getId(), 1));
        index.putWord("b", new WordOccurence(doc3.getId(), 2));
        index.putWord("c", new WordOccurence(doc3.getId(), 4));

        Scorer scorer = (index1, document, query) -> {
            if (document == doc1.getId()) {
                return 3;
            } else if (document == doc2.getId()) {
                return 2;
            } else {
                return 1;
            }
        };
        List<String> query = Arrays.asList("a", "b", "c");

        // act
        List<SearchResult> result = Searcher.getInstance().search(index, query, scorer, 2);

        // assert
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getDocumentName(), is("doc1"));
        assertThat(result.get(0).getScore(), is(3.0));
        assertThat(result.get(0).getRank(), is(0));
        assertThat(result.get(1).getDocumentName(), is("doc2"));
        assertThat(result.get(1).getScore(), is(2.0));
        assertThat(result.get(1).getRank(), is(1));
    }
}