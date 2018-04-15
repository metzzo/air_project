import indexer.InvertedIndex;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.jupiter.api.Assertions.*;


class InvertedIndexTest {
    @Test
    void isMergeWorking() {
        // arrange
        InvertedIndex a = new InvertedIndex();
        a.putWord("a", new InvertedIndex.WordOccurence("doc1", 1));
        a.putWord("b", new InvertedIndex.WordOccurence("doc2", 1));

        InvertedIndex b = new InvertedIndex();
        b.putWord("b", new InvertedIndex.WordOccurence("doc2", 1));
        b.putWord("b", new InvertedIndex.WordOccurence("doc3", 1));
        b.putWord("c", new InvertedIndex.WordOccurence("doc4", 1));

        InvertedIndex expected = new InvertedIndex();
        expected.putWord("a", new InvertedIndex.WordOccurence("doc1", 1));
        expected.putWord("b", new InvertedIndex.WordOccurence("doc2", 2));
        expected.putWord("b", new InvertedIndex.WordOccurence("doc3", 1));
        expected.putWord("c", new InvertedIndex.WordOccurence("doc4", 1));

        // act
        a.merge(b);

        // assert
        assertThat(a, is(expected));

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
    }

    @Test
    void isMergeSameWorking() {
        // arrange
        InvertedIndex a = new InvertedIndex();
        a.putWord("a", new InvertedIndex.WordOccurence("doc1", 1));

        InvertedIndex b = new InvertedIndex();
        b.putWord("a", new InvertedIndex.WordOccurence("doc1", 1));

        InvertedIndex expected = new InvertedIndex();
        expected.putWord("a", new InvertedIndex.WordOccurence("doc1", 2));

        // act
        a.merge(b);

        // assert
        assertThat(a, is(expected));
    }

    @Test
    void isPutWordWorking() {
        // arrange
        InvertedIndex a = new InvertedIndex();

        // act
        a.putWord("a", new InvertedIndex.WordOccurence("doc1", 1));
        a.putWord("a", new InvertedIndex.WordOccurence("doc2", 1));
        a.putWord("a", new InvertedIndex.WordOccurence("doc2", 1));
        a.putWord("b", new InvertedIndex.WordOccurence("doc3", 1));

        // assert
        Map<String, InvertedIndex.IndexValue> index = a.getIndex();

        assertThat(index.size(), is(2));
        assertThat(index.get("a"), is(
                new InvertedIndex.IndexValue(
                        new HashSet<>(
                                Arrays.asList(
                                        new InvertedIndex.WordOccurence("doc1", 1),
                                        new InvertedIndex.WordOccurence("doc2", 2)
                                )
                        )
                )
        ));
        assertThat(index.get("b"), is(
                new InvertedIndex.IndexValue(
                        new InvertedIndex.WordOccurence("doc3", 1)
                )
        ));
    }
}