package indexer;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DocumentRepository {
    private static Integer globalIdCounter = 0;

    private Map<String, DocumentInfo> documentInfosByName;
    private Map<Integer, DocumentInfo> documentInfosById;
    private double averageDocumentSize = -1;
    private double meanAverageTermFrequency;

    public DocumentRepository() {
        this.documentInfosByName = new HashMap<>();
        this.documentInfosById = new HashMap<>();
    }

    public DocumentRepository(int numOfDocuments) {
        this.documentInfosByName = new HashMap<>(numOfDocuments);
        this.documentInfosById = new HashMap<>(numOfDocuments);
    }

    public DocumentInfo register(String document, int size) {
        DocumentInfo info = new DocumentInfo(document, -1, size);
        return this.register(info);
    }

    public DocumentInfo register(DocumentInfo info) {
        if (this.documentInfosByName.containsKey(info.getName())) {
            DocumentInfo old = this.documentInfosByName.get(info.getName());
            old.setSize(info.getSize());
            old.setMaxFrequencyOfTerm(info.getMaxFrequencyOfTerm());
            old.setAverageTermFrequency(info.getAverageTermFrequency());

            info = old;
        } else {
            if (info.getId() == -1) {
                info.setId(globalIdCounter);

                // this ensures that IDs are unique in every document
                synchronized (globalIdCounter) {
                    globalIdCounter++;
                }
            }

            this.documentInfosByName.put(info.getName(), info);
            this.documentInfosById.put(info.getId(), info);
        }
        return info;
    }

    public void clear() {
        this.documentInfosByName.clear();
        this.documentInfosById.clear();
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

    public void calculateMetrics() {
        this.averageDocumentSize = 0.0;
        this.meanAverageTermFrequency = 0.0;
        for (DocumentInfo info : this.documentInfosByName.values()) {
            this.averageDocumentSize += info.getSize();
            this.meanAverageTermFrequency += info.getAverageTermFrequency();
        }
        this.averageDocumentSize /= this.documentInfosByName.size();
        this.meanAverageTermFrequency /= this.documentInfosByName.size();
    }

    public double getAverageDocumentSize() {
        return this.averageDocumentSize;
    }

    public double getMeanAverageTermFrequency() {
        return meanAverageTermFrequency;
    }

    public void serialize(OutputStream stream) {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(stream));
        try {
            dos.writeInt(43);
            dos.writeDouble(this.averageDocumentSize);
            dos.writeDouble(this.meanAverageTermFrequency);
            dos.writeInt(this.documentInfosById.size());
            for (Integer id : this.documentInfosById.keySet()) {
                DocumentInfo info = this.documentInfosById.get(id);
                dos.writeInt(info.getId());
                dos.writeInt(info.getSize());
                dos.writeInt(info.getMaxFrequencyOfTerm());
                dos.writeDouble(info.getAverageTermFrequency());
                dos.writeUTF(info.getName());
            }
            dos.writeInt(43);
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DocumentRepository deserialize(InputStream stream) {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(stream));
            if (dis.readInt() != 43) {
                throw new RuntimeException("Unexpected format");
            }
            double avgDocumentSize = dis.readDouble();
            double meanAvgTermFrequency = dis.readDouble();
            int numOfDocuments = dis.readInt();
            System.out.println("Deserializing DocumentRepository");

            DocumentRepository repo = new DocumentRepository(numOfDocuments);
            repo.averageDocumentSize = avgDocumentSize;
            repo.meanAverageTermFrequency = meanAvgTermFrequency;
            double lastpercent = 0.0;
            for (int i = 0; i < numOfDocuments; i++) {
                DocumentInfo info = new DocumentInfo();
                info.setId(dis.readInt());
                info.setSize(dis.readInt());
                info.setMaxFrequencyOfTerm(dis.readInt());
                info.setAverageTermFrequency(dis.readDouble());
                info.setName(dis.readUTF());
                repo.register(info);
                double percent = i / (double)numOfDocuments;
                if (percent >= lastpercent + 0.1) {
                    System.out.println((int)(percent*100) + "%");
                    lastpercent = percent;
                }

            }

            if (dis.readInt() != 43) {
                throw new RuntimeException("Unexpected format");
            }

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
            dis.close();
            return repo;
        } catch (IOException e) {
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

        if (averageDocumentSize != other.averageDocumentSize) {
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
