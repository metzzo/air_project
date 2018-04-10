package indexer;

import java.util.List;
import java.util.Map;

public class InvertedIndex {
    public static class IndexKey {
        int count;
        String document;
    }

    public static class IndexValue {
        List<String> words;
    }

    private Map<IndexKey, IndexValue> index;

}
