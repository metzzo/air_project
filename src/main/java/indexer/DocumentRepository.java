package indexer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class DocumentRepository {
    private static Integer globalIdCounter = 0;

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

    public DocumentInfo register(String document, int size) {
        DocumentInfo info = new DocumentInfo(document, -1, size);
        return this.register(info);
    }

    public DocumentInfo register(DocumentInfo info) {
        if (this.documentInfosByName.containsKey(info.getName())) {
            DocumentInfo old = this.documentInfosByName.get(info.getName());
            old.setSize(info.getSize());
            old.setMaxFrequencyOfWord(info.getMaxFrequencyOfWord());

            info = old;
        } else {
            info.setId(globalIdCounter);

            // this ensures that IDs are unique in every document
            synchronized (globalIdCounter) { globalIdCounter++; }

            this.documentInfosByName.put(info.getName(), info);
            this.documentInfosById.put(info.getId(), info);
        }
        this.dirty = true;
        return info;
    }

    public void clear() {
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

    public void merge(DocumentRepository other) {
        if (other == this) {
            return;
        }

        for (String documents : other.documentInfosByName.keySet()) {
            DocumentInfo newInfo = new DocumentInfo(other.documentInfosByName.get(documents));

            this.register(newInfo);
        }
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

    public void serialize(OutputStream stream) {
        try {
            JAXBContext jc = JAXBContext.newInstance(DocumentRepository.class);
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this, stream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static DocumentRepository deserialize(InputStream stream) {
        try {
            JAXBContext jc = JAXBContext.newInstance(DocumentRepository.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            //marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            DocumentRepository repo = (DocumentRepository)unmarshaller.unmarshal(stream);

            synchronized (globalIdCounter) {
                // ensure that globalIdCounter is above limit => so it stays unique
                int currentIndex = globalIdCounter;
                for (Integer id : repo.getDocumentIDs()) {
                    if (id >= currentIndex) {
                        currentIndex = id + 1;
                    }
                }
                globalIdCounter = currentIndex;
            }

            return repo;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<Integer> getDocumentIDs() {
        return this.documentInfosById.keySet();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DocumentRepository) || getClass() != obj.getClass()) {
            return false;
        }
        DocumentRepository other = (DocumentRepository)obj;

        if (averageDocumentSize != other.averageDocumentSize || dirty != other.dirty) {
            return false;
        }

        for (String name : this.documentInfosByName.keySet()) {
            DocumentInfo myDoc = this.documentInfosByName.get(name);
            DocumentInfo otherDoc = other.documentInfosByName.get(name);
            if (!myDoc.equals(otherDoc)) {
                return false;
            }
        }

        for (String name : this.documentInfosByName.keySet()) {
            DocumentInfo myDoc = this.documentInfosByName.get(name);
            DocumentInfo otherDoc = other.documentInfosByName.get(name);
            if (!myDoc.equals(otherDoc)) {
                return false;
            }
        }

        return true;
    }
}
