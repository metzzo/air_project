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
}
