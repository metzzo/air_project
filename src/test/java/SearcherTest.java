import indexer.DocumentRepository;
import indexer.InvertedIndex;
import indexer.WordOccurence;
import org.junit.jupiter.api.Test;
import score.Scorer;
import score.TFIDFScore;
import search.SearchResult;
import search.Searcher;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class SearcherTest {
    @Test
    void simpleSearchWorking() {
        // arrange
        DocumentRepository.getInstance().clear();

        InvertedIndex index = new InvertedIndex();
        index.putWord("a", new WordOccurence("doc1", 4));
        index.putWord("b", new WordOccurence("doc1", 2));
        index.putWord("c", new WordOccurence("doc1", 1));


        index.putWord("a", new WordOccurence("doc2", 8));
        index.putWord("b", new WordOccurence("doc2", 1));
        index.putWord("c", new WordOccurence("doc2", 1));


        index.putWord("a", new WordOccurence("doc3", 1));
        index.putWord("b", new WordOccurence("doc3", 2));
        index.putWord("c", new WordOccurence("doc3", 4));


        DocumentRepository.getInstance().register("doc1", 3);
        DocumentRepository.getInstance().register("doc2", 3);
        DocumentRepository.getInstance().register("doc3", 3);

        Scorer scorer = (index1, document, query) -> {
            switch (document) {
                case "doc1":
                    return 3;
                case "doc2":
                    return 2;
                default:
                    return 1;
            }
        };
        List<String> query = Arrays.asList("a", "b", "c");

        // act
        List<SearchResult> result = Searcher.getInstance().search(index, query, scorer, 2);

        // assert
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getDocument(), is("doc1"));
        assertThat(result.get(0).getScore(), is(3.0));
        assertThat(result.get(0).getRank(), is(0));
        assertThat(result.get(1).getDocument(), is("doc2"));
        assertThat(result.get(1).getScore(), is(2.0));
        assertThat(result.get(1).getRank(), is(1));
    }
}