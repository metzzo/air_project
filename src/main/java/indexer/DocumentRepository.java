package indexer;

import java.util.HashMap;
import java.util.Map;

public class DocumentRepository {
    private static DocumentRepository documentRepository = new DocumentRepository();

    public static DocumentRepository getInstance() {
        return documentRepository;
    }


    private Map<String, Integer> documentSize;
    private double averageDocumentSize;
    private boolean dirty;

    private DocumentRepository() {
        this.documentSize = new HashMap<>();
    }

    synchronized public void register(String document, int size) {
        this.documentSize.put(document, size);
        this.dirty = true;
    }

    synchronized public void clear() {
        this.documentSize.clear();
        this.dirty = true;
    }

    public int getDocumentSize(String document) {
        if (this.documentSize.containsKey(document)) {
            return this.documentSize.get(document);
        } else {
            return 0;
        }
    }

    public double getAverageDocumentSize() {
        synchronized (this) {
            if (this.dirty) {
                this.averageDocumentSize = 0.0;
                for (Integer size : this.documentSize.values()) {
                    this.averageDocumentSize += size;
                }
                this.averageDocumentSize /= this.documentSize.size();

                this.dirty = false;
            }
        }
        return this.averageDocumentSize;
    }
}
