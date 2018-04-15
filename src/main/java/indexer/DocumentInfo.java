package indexer;

public class DocumentInfo {
    private String name;
    private int id;
    private int size;

    public DocumentInfo(String name, int id, int size) {
        this.name = name;
        this.id = id;
        this.size = size;
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

    public void setSize(int size) {
        this.size = size;
    }
}
