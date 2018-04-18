package indexer;

public class DocumentInfo {
    private String name;
    private int id;
    private int size;
    private int maxFrequencyOfTerm;
    private double averageTermFrequency;

    public DocumentInfo(String name, int id, int size) {
        this.name = name;
        this.id = id;
        this.size = size;
        this.maxFrequencyOfTerm = 0;
    }

    public DocumentInfo() {

    }

    public DocumentInfo(DocumentInfo documentInfo) {
        this.name = documentInfo.name;
        this.id = documentInfo.id;
        this.size = documentInfo.size;
        this.maxFrequencyOfTerm = documentInfo.maxFrequencyOfTerm;
        this.averageTermFrequency = documentInfo.averageTermFrequency;
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

    public int getMaxFrequencyOfTerm() {
        return maxFrequencyOfTerm;
    }

    public double getAverageTermFrequency() {
        return averageTermFrequency;
    }

    public void  setSize(int size) {
        this.size = size;
    }

    public void setMaxFrequencyOfTerm(int maxFrequencyOfTerm) {
        this.maxFrequencyOfTerm = maxFrequencyOfTerm;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAverageTermFrequency(double averageTextFrequency) {
        this.averageTermFrequency = averageTextFrequency;
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
        return this.name.equals(other.name) && this.size == other.size && this.maxFrequencyOfTerm == other.maxFrequencyOfTerm;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() + this.size * 100 + this.maxFrequencyOfTerm * 1000;
    }
}
