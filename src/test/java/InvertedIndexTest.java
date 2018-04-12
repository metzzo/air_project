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
        a.putWord("a", "doc1");
        a.putWord("b", "doc2");

        InvertedIndex b = new InvertedIndex();
        b.putWord("b", "doc2");
        b.putWord("b", "doc3");
        b.putWord("c", "doc4");

        InvertedIndex expected = new InvertedIndex();
        expected.putWord("a", "doc1");
        expected.putWord("b", "doc2");
        expected.putWord("b", "doc3");
        expected.putWord("c", "doc4");

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
        a.putWord("a", "doc1");
        a.putWord("a", "doc2");
        a.putWord("a", "doc2");
        a.putWord("b", "doc3");

        // assert
        Map<String, InvertedIndex.IndexValue> index = a.getIndex();

        assertThat(index.size(), is(2));
        assertThat(index.get("a"), is(new InvertedIndex.IndexValue(new HashSet<>(Arrays.asList(new String[] { "doc1", "doc2" })))));
        assertThat(index.get("b"), is(new InvertedIndex.IndexValue("doc3")));
    }
}