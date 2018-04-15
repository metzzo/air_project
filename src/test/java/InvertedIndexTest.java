import indexer.IndexValue;
import indexer.InvertedIndex;
import indexer.WordOccurence;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


class InvertedIndexTest {
    @Test
    void isMergeWorking() {
        // arrange
        InvertedIndex a = new InvertedIndex();
        a.putWord("a", new WordOccurence("doc1", 1));
        a.putWord("b", new WordOccurence("doc2", 1));

        InvertedIndex b = new InvertedIndex();
        b.putWord("b", new WordOccurence("doc2", 1));
        b.putWord("b", new WordOccurence("doc3", 1));
        b.putWord("c", new WordOccurence("doc4", 1));

        InvertedIndex expected = new InvertedIndex();
        expected.putWord("a", new WordOccurence("doc1", 1));
        expected.putWord("b", new WordOccurence("doc2", 2));
        expected.putWord("b", new WordOccurence("doc3", 1));
        expected.putWord("c", new WordOccurence("doc4", 1));

        // act
        a.merge(b);

        // assert
        assertThat(a, is(expected));
        assertThat(a.getNumDocuments(), is(4));

    }

    @Test
    void isMergeEmptyWorking() {
        // arrange
        InvertedIndex a = new InvertedIndex();
        InvertedIndex b = new InvertedIndex();
        InvertedIndex expected = new InvertedIndex();

        // act
        a.merge(b);

        // assert
        assertThat(a, is(expected));
        assertThat(a.getNumDocuments(), is(0));
    }

    @Test
    void isMergeSameWorking() {
        // arrange
        InvertedIndex a = new InvertedIndex();
        a.putWord("a", new WordOccurence("doc1", 1));

        InvertedIndex b = new InvertedIndex();
        b.putWord("a", new WordOccurence("doc1", 1));

        InvertedIndex expected = new InvertedIndex();
        expected.putWord("a", new WordOccurence("doc1", 2));

        // act
        a.merge(b);

        // assert
        assertThat(a, is(expected));
        assertThat(a.getNumDocuments(), is(1));
    }

    @Test
    void isPutWordWorking() {
        // arrange
        InvertedIndex a = new InvertedIndex();

        // act
        a.putWord("a", new WordOccurence("doc1", 1));
        a.putWord("a", new WordOccurence("doc2", 1));
        a.putWord("a", new WordOccurence("doc2", 1));
        a.putWord("b", new WordOccurence("doc3", 1));

        // assert
        Map<String, IndexValue> index = a.getIndex();

        assertThat(index.size(), is(2));
        assertThat(index.get("a"), is(
                new IndexValue(
                        new HashSet<>(
                                Arrays.asList(
                                        new WordOccurence("doc1", 1),
                                        new WordOccurence("doc2", 2)
                                )
                        )
                )
        ));
        assertThat(index.get("b"), is(
                new IndexValue(
                        new WordOccurence("doc3", 1)
                )
        ));
        assertThat(a.getNumDocuments(), is(3));
    }
}