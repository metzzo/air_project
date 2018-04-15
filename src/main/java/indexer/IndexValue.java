package indexer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IndexValue {
    Map<String, Integer> documents = new HashMap<>();

    public IndexValue(Set<WordOccurence> documents) {
        for (WordOccurence occurence : documents) {
            this.putWord(occurence);
        }
    }

    public IndexValue(WordOccurence occurence) {
        this.putWord(occurence);
    }

    public Map<String, Integer> getDocuments() {
        return this.documents;
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
}
