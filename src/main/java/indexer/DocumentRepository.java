package indexer;

import java.util.HashMap;
import java.util.Map;

public class DocumentRepository {
    private static DocumentRepository documentRepository = new DocumentRepository();

    public static DocumentRepository getInstance() {
        return documentRepository;
    }


    private Map<String, Integer> documentSize;
    private DocumentRepository() {
        this.documentSize = new HashMap<>();
    }

    synchronized public void register(String document, int size) {
        this.documentSize.put(document, size);
    }

    synchronized public void clear() {
        this.documentSize.clear();
    }

    public int getDocumentSize(String document) {
        if (this.documentSize.containsKey(document)) {
            return this.documentSize.get(document);
        } else {
            return 0;
        }
    }
}
