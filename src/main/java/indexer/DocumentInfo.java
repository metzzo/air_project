package indexer;

public class DocumentInfo {
    private String name;
    private int id;
    private int size;
    private int maxFrequencyOfWord;

    public DocumentInfo(String name, int id, int size) {
        this.name = name;
        this.id = id;
        this.size = size;
        this.maxFrequencyOfWord = 0;
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
}
