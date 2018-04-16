import indexer.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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

        assertThat(repo.getAverageDocumentSize(), is((10.0 + 5.0 + 1.0) / 3.0));
    }
}