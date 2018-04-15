import indexer.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.junit.jupiter.api.Assertions.*;

class DocumentRepositoryTest {
    @BeforeEach
    void setUp() {
        DocumentRepository.getInstance().clear();
    }

    @Test
    void registerAndGetDocumentSizeWorks() {
        DocumentRepository.getInstance().register("doc1", 10);
        DocumentRepository.getInstance().register("doc2", 5);
        DocumentRepository.getInstance().register("doc3", 1);

        assertThat(DocumentRepository.getInstance().getDocumentByName("doc1").getSize(), is(10));
        assertThat(DocumentRepository.getInstance().getDocumentByName("doc2").getSize(), is(5));
        assertThat(DocumentRepository.getInstance().getDocumentByName("doc3").getSize(), is(1));
    }

    @Test
    void getAverageDocumentSizeWorks() {
        DocumentRepository.getInstance().register("doc1", 10);
        DocumentRepository.getInstance().register("doc2", 5);
        DocumentRepository.getInstance().register("doc3", 1);

        assertThat(DocumentRepository.getInstance().getAverageDocumentSize(), is((10.0 + 5.0 + 1.0) / 3.0));
    }
}