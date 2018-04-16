package indexer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class DocumentInfo {
    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "id")
    private int id;
    @XmlElement(name = "size")
    private int size;
    @XmlElement(name = "maxfrequencyofword")
    private int maxFrequencyOfWord;

    public DocumentInfo(String name, int id, int size) {
        this.name = name;
        this.id = id;
        this.size = size;
        this.maxFrequencyOfWord = 0;
    }

    public DocumentInfo() {

    }

    public DocumentInfo(DocumentInfo documentInfo) {
        this.name = documentInfo.name;
        this.id = documentInfo.id;
        this.size = documentInfo.size;
        this.maxFrequencyOfWord = documentInfo.maxFrequencyOfWord;
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public int getMaxFrequencyOfWord() {
        return maxFrequencyOfWord;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setMaxFrequencyOfWord(int maxFrequencyOfWord) {
        this.maxFrequencyOfWord = maxFrequencyOfWord;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DocumentInfo) || getClass() != obj.getClass()) {
            return false;
        }
        DocumentInfo other = (DocumentInfo)obj;
        return this.name.equals(other.name) && this.size == other.size && this.maxFrequencyOfWord == other.maxFrequencyOfWord;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() + this.size * 100 + this.maxFrequencyOfWord * 1000;
    }
}
