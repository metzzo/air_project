import indexer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


class InvertedIndexTest {
    @BeforeEach
    void setUp() {
        DocumentRepository.getInstance().clear();
    }

    @Test
    void isMergeWorking() {
        // arrange
        DocumentInfo doc1 = DocumentRepository.getInstance().register("doc1", 0);
        DocumentInfo doc2 = DocumentRepository.getInstance().register("doc2", 0);
        DocumentInfo doc3 = DocumentRepository.getInstance().register("doc3", 0);
        DocumentInfo doc4 = DocumentRepository.getInstance().register("doc4", 0);

        InvertedIndex a = new InvertedIndex();
        a.putWord("a", new WordOccurence(doc1.getId(), 1));
        a.putWord("b", new WordOccurence(doc2.getId(), 1));

        InvertedIndex b = new InvertedIndex();
        b.putWord("b", new WordOccurence(doc2.getId(), 1));
        b.putWord("b", new WordOccurence(doc3.getId(), 1));
        b.putWord("c", new WordOccurence(doc4.getId(), 1));

        InvertedIndex expected = new InvertedIndex();
        expected.putWord("a", new WordOccurence(doc1.getId(), 1));
        expected.putWord("b", new WordOccurence(doc2.getId(), 2));
        expected.putWord("b", new WordOccurence(doc3.getId(), 1));
        expected.putWord("c", new WordOccurence(doc4.getId(), 1));

        // act
        a.merge(b);

        // assert
        assertThat(a, is(expected));
        assertThat(a.getNumDocuments(), is(4));
        assertThat(a.getMaxFrequencyInDocument(doc1.getId()), is(1));
        assertThat(a.getMaxFrequencyInDocument(doc2.getId()), is(2));
        assertThat(a.getMaxFrequencyInDocument(doc3.getId()), is(1));
        assertThat(a.getMaxFrequencyInDocument(doc4.getId()), is(1));

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
        DocumentInfo doc1 = DocumentRepository.getInstance().register("doc1", 0);

        InvertedIndex a = new InvertedIndex();
        a.putWord("a", new WordOccurence(doc1.getId(), 1));

        InvertedIndex b = new InvertedIndex();
        b.putWord("a", new WordOccurence(doc1.getId(), 1));

        InvertedIndex expected = new InvertedIndex();
        expected.putWord("a", new WordOccurence(doc1.getId(), 2));

        // act
        a.merge(b);

        // assert
        assertThat(a, is(expected));
        assertThat(a.getNumDocuments(), is(1));
        assertThat(a.getMaxFrequencyInDocument(doc1.getId()), is(2));
    }

    @Test
    void isPutWordWorking() {
        // arrange
        DocumentInfo doc1 = DocumentRepository.getInstance().register("doc1", 0);
        DocumentInfo doc2 = DocumentRepository.getInstance().register("doc2", 0);
        DocumentInfo doc3 = DocumentRepository.getInstance().register("doc3", 0);
        InvertedIndex a = new InvertedIndex();

        // act
        a.putWord("a", new WordOccurence(doc1.getId(), 1));
        a.putWord("a", new WordOccurence(doc2.getId(), 1));
        a.putWord("a", new WordOccurence(doc2.getId(), 1));
        a.putWord("b", new WordOccurence(doc3.getId(), 1));

        // assert
        assertThat(a.getNumWords(), is(2));
        assertThat(a.findByWord("a"), is(
                new IndexValue(
                        new HashSet<>(
                                Arrays.asList(
                                        new WordOccurence(doc1.getId(), 1),
                                        new WordOccurence(doc2.getId(), 2)
                                )
                        )
                )
        ));
        assertThat(a.findByWord("b"), is(
                new IndexValue(
                        new WordOccurence(doc3.getId(), 1)
                )
        ));
        assertThat(a.getNumDocuments(), is(3));
        assertThat(a.getMaxFrequencyInDocument(doc1.getId()), is(1));
        assertThat(a.getMaxFrequencyInDocument(doc2.getId()), is(2));
        assertThat(a.getMaxFrequencyInDocument(doc3.getId()), is(1));
    }

    @Test
    void isMaxFrequencyOfWordWorking() {
        // arrange
        DocumentInfo doc1 = DocumentRepository.getInstance().register("doc1", 0);
        DocumentInfo doc2 = DocumentRepository.getInstance().register("doc2", 0);
        InvertedIndex a = new InvertedIndex();

        // act
        a.putWord("a", new WordOccurence(doc1.getId(), 1));
        a.putWord("a", new WordOccurence(doc1.getId(), 2));
        a.putWord("a", new WordOccurence(doc1.getId(), 3));
        a.putWord("a", new WordOccurence(doc1.getId(), 4));
        a.putWord("a", new WordOccurence(doc2.getId(), 1));

        // assert
        assertThat(a.getMaxFrequencyInDocument(doc1.getId()), is(1 + 2 + 3 + 4));
        assertThat(a.getMaxFrequencyInDocument(doc2.getId()), is(1));

    }

    @Test
    void serializeDeserializeWorking() {
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

        // act
        ByteArrayOutputStream outstr = new ByteArrayOutputStream();
        index.serialize(outstr);
        InputStream instr = new ByteArrayInputStream(outstr.toByteArray());
        InvertedIndex newIndex = InvertedIndex.deserialize(instr);

        // assert
        assertThat(index, is(newIndex));
    }
}