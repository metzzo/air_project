package indexer;

import java.util.HashMap;
import java.util.Map;

public class DocumentRepository {

    private static DocumentRepository documentRepository = new DocumentRepository();

    public static DocumentRepository getInstance() {
        return documentRepository;
    }


    private Map<String, DocumentInfo> documentInfosByName;
    private Map<Integer, DocumentInfo> documentInfosById;
    private double averageDocumentSize;
    private boolean dirty;

    private DocumentRepository() {
        this.documentInfosByName = new HashMap<>();
        this.documentInfosById = new HashMap<>();
    }

    synchronized public DocumentInfo register(String document, int size) {
        DocumentInfo info;
        if (this.documentInfosByName.containsKey(document)) {
            info = this.documentInfosByName.get(document);
            info.setSize(size);
        } else {
            info = new DocumentInfo(document, this.documentInfosByName.size(), size);
            this.documentInfosByName.put(info.getName(), info);
            this.documentInfosById.put(info.getId(), info);
        }
        this.dirty = true;
        return info;
    }

    synchronized public void clear() {
        this.documentInfosByName.clear();
        this.dirty = true;
    }

    public DocumentInfo getDocumentByName(String document) {
        return this.documentInfosByName.getOrDefault(document, null);
    }

    public DocumentInfo getDocumentById(int id) {
        return this.documentInfosById.getOrDefault(id, null);
    }

    public double getAverageDocumentSize() {
        synchronized (this) {
            if (this.dirty) {
                this.averageDocumentSize = 0.0;
                for (DocumentInfo info : this.documentInfosByName.values()) {
                    this.averageDocumentSize += info.getSize();
                }
                this.averageDocumentSize /= this.documentInfosByName.size();

                this.dirty = false;
            }
        }
        return this.averageDocumentSize;
    }
}
