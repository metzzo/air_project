package indexer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.NONE)
public class DocumentRepository {
    @XmlElement(name = "byname")
    private Map<String, DocumentInfo> documentInfosByName;
    @XmlElement(name = "byid")
    private Map<Integer, DocumentInfo> documentInfosById;
    @XmlElement(name = "avgsize")
    private double averageDocumentSize;
    @XmlElement(name = "dirty")
    private boolean dirty;

    public DocumentRepository() {
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
        this.documentInfosById.clear();
        this.dirty = true;
    }

    public DocumentInfo getDocumentByName(String document) {
        return this.documentInfosByName.getOrDefault(document, null);
    }

    public DocumentInfo getDocumentById(int id) {
        return this.documentInfosById.getOrDefault(id, null);
    }

    public int getNumDocuments() {
        return this.documentInfosByName.size();
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

    public void serialize() {

    }

    public static DocumentRepository deserialize(FileInputStream fis) {
        return null;
    }
}
