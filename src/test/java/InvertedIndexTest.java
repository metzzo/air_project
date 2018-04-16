import indexer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


class InvertedIndexTest {
    private DocumentRepository repo;

    @BeforeEach
    void setUp() {
        this.repo = new DocumentRepository();
    }

    @Test
    void isMergeWorking() {
        // arrange
        DocumentInfo doc1 = repo.register("doc1", 0);
        DocumentInfo doc2 = repo.register("doc2", 0);
        DocumentInfo doc3 = repo.register("doc3", 0);
        DocumentInfo doc4 = repo.register("doc4", 0);

        InvertedIndex a = new InvertedIndex(repo);
        a.putWord("a", new WordOccurence(doc1.getId(), 1));
        a.putWord("b", new WordOccurence(doc2.getId(), 1));

        InvertedIndex b = new InvertedIndex(repo);
        b.putWord("b", new WordOccurence(doc2.getId(), 1));
        b.putWord("b", new WordOccurence(doc3.getId(), 1));
        b.putWord("c", new WordOccurence(doc4.getId(), 1));

        InvertedIndex expected = new InvertedIndex(repo);
        expected.putWord("a", new WordOccurence(doc1.getId(), 1));
        expected.putWord("b", new WordOccurence(doc2.getId(), 2));
        expected.putWord("b", new WordOccurence(doc3.getId(), 1));
        expected.putWord("c", new WordOccurence(doc4.getId(), 1));

        // act
        a.merge(b);

        // assert
        assertThat(a, is(expected));
    }

    @Test
    void isMergeEmptyWorking() {
        // arrange
        InvertedIndex a = new InvertedIndex(repo);
        InvertedIndex b = new InvertedIndex(repo);
        InvertedIndex expected = new InvertedIndex(repo);

        // act
        a.merge(b);

        // assert
        assertThat(a, is(expected));
    }

    @Test
    void isMergeSameWorking() {
        // arrange
        DocumentInfo doc1 = repo.register("doc1", 0);

        InvertedIndex a = new InvertedIndex(repo);
        a.putWord("a", new WordOccurence(doc1.getId(), 1));

        InvertedIndex b = new InvertedIndex(repo);
        b.putWord("a", new WordOccurence(doc1.getId(), 1));

        InvertedIndex expected = new InvertedIndex(repo);
        expected.putWord("a", new WordOccurence(doc1.getId(), 2));

        // act
        a.merge(b);

        // assert
        assertThat(a, is(expected));
    }

    @Test
    void isPutWordWorking() {
        // arrange
        DocumentInfo doc1 = repo.register("doc1", 0);
        DocumentInfo doc2 = repo.register("doc2", 0);
        DocumentInfo doc3 = repo.register("doc3", 0);
        InvertedIndex a = new InvertedIndex(repo);

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
    }

    @Test
    void serializeDeserializeWorking() {
        // arrange
        DocumentInfo doc1 = repo.register("doc1", 0);
        DocumentInfo doc2 = repo.register("doc2", 0);
        DocumentInfo doc3 = repo.register("doc3", 0);

        InvertedIndex index = new InvertedIndex(repo);
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
        InvertedIndex newIndex = InvertedIndex.deserialize(repo, instr);

        // assert
        assertThat(index, is(newIndex));
    }
}