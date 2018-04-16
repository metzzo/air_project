import indexer.DocumentInfo;
import indexer.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

class DocumentRepositoryTest {
    private DocumentRepository repo;

    @BeforeEach
    void setUp() {
        repo = new DocumentRepository();
    }

    @Test
    void registerAndGetDocumentSizeWorks() {
        repo.register("doc1", 10);
        repo.register("doc2", 5);
        repo.register("doc3", 1);

        assertThat(repo.getDocumentByName("doc1").getSize(), is(10));
        assertThat(repo.getDocumentByName("doc2").getSize(), is(5));
        assertThat(repo.getDocumentByName("doc3").getSize(), is(1));
    }

    @Test
    void getAverageDocumentSizeWorks() {
        repo.register("doc1", 10);
        repo.register("doc2", 5);
        repo.register("doc3", 1);
        repo.calculateAverageDocumentSize();

        assertThat(repo.getAverageDocumentSize(), is((10.0 + 5.0 + 1.0) / 3.0));
    }

    @Test
    void mergeWorks() {
        // arrange
        repo.register("doc1", 10);
        repo.register("doc2", 5);
        repo.register("doc3", 1);

        DocumentRepository repo2 = new DocumentRepository();
        repo2.register("doc4", 5);
        repo2.register("doc5", 1);

        DocumentRepository expectedRepo = new DocumentRepository();
        expectedRepo.register("doc1", 10);
        expectedRepo.register("doc2", 5);
        expectedRepo.register("doc3", 1);
        expectedRepo.register("doc4", 5);
        expectedRepo.register("doc5", 1);

        // act
        repo.merge(repo2);

        // assert
        assertThat(repo, is(expectedRepo));
    }

    @Test
    void serializeDeserializeWorks() {
        // arrange
        DocumentInfo doc = repo.register("doc1", 10);
        doc.setMaxFrequencyOfWord(5);
        repo.register("doc2", 5);
        repo.register("doc3", 1);

        // act
        ByteArrayOutputStream outstr = new ByteArrayOutputStream();
        repo.serialize(outstr);
        InputStream instr = new ByteArrayInputStream(outstr.toByteArray());
        DocumentRepository newRepo = DocumentRepository.deserialize(instr);

        // assert
        assertThat(repo, is(newRepo));
    }

    @Test
    void equalsWithSameInstancesWorks() {
        DocumentInfo doc = repo.register("doc1", 10);
        doc.setMaxFrequencyOfWord(5);
        repo.register("doc2", 5);
        repo.register("doc3", 1);

        DocumentRepository repo2 = new DocumentRepository();
        doc = repo2.register("doc1", 10);
        doc.setMaxFrequencyOfWord(5);
        repo2.register("doc2", 5);
        repo2.register("doc3", 1);

        assertThat(repo, is(repo2));
    }

    @Test
    void equalsWithDifferentInstancesWorks() {
        DocumentInfo doc = repo.register("doc1", 10);
        doc.setMaxFrequencyOfWord(4);
        repo.register("doc2", 5);
        repo.register("doc3", 1);

        DocumentRepository repo2 = new DocumentRepository();
        doc = repo2.register("doc1", 10);
        doc.setMaxFrequencyOfWord(5);
        repo2.register("doc2", 5);
        repo2.register("doc3", 1);

        assertThat(repo, is(not(repo2)));
    }
}