package indexer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.*;

@XmlAccessorType(XmlAccessType.NONE)
public class IndexValue {
    @XmlElement(name = "dcs")
    private Map<String, Integer> documents = new HashMap<>();

    public IndexValue(Set<WordOccurence> documents) {
        for (WordOccurence occurence : documents) {
            this.putWord(occurence);
        }
    }

    public IndexValue() {
        this(new HashSet<>());
    }

    public IndexValue(WordOccurence occurence) {
        this.putWord(occurence);
    }

    public int getFrequencyInDocument(String document) {
        return this.documents.get(document);
    }

    public boolean isInDocument(String document) {
        return this.documents.containsKey(document);
    }

    public int getNumDocuments() {
        return this.documents.size();
    }

    void putWord(WordOccurence occurence) {
        if (this.documents.containsKey(occurence.document)) {
            int oldCount = this.documents.get(occurence.document);
            this.documents.put(occurence.document, oldCount + occurence.count);
        } else {
            this.documents.put(occurence.document, occurence.count);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IndexValue) || getClass() != obj.getClass()) {
            return false;
        }
        IndexValue other = (IndexValue)obj;
        return other.documents.equals(this.documents);
    }

    public Set<String> getAllDocuments() {
        return this.documents.keySet();
    }

    public Map<String, Integer> getFrequenciesForDocuments() {
        return this.documents;
    }
}
