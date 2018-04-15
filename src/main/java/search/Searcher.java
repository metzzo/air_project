package search;

import indexer.IndexValue;
import indexer.InvertedIndex;
import score.Scorer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Searcher {
    private static class SearchResult implements Comparable<SearchResult> {
        String document;
        double score;
        int rank;

        @Override
        public int compareTo(SearchResult o) {
            double delta = o.score - this.score;
            if (delta == 0.0) {
                return 0;
            } else if (delta < 0.0) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private static Searcher searcher = new Searcher();

    public static Searcher getInstance() {
        return searcher;
    }

    private Searcher() { }


    public List<SearchResult> search(InvertedIndex index, List<String> query, Scorer scorer, int maxNum) {
        // get candidate documents by chosing documents that contain at least 1 term of the query
        List<String> contenders = new LinkedList<>();
        for (String word : query) {
            IndexValue val = index.findByWord(word);
            contenders.addAll(val.getAllDocuments());
        }

        List<SearchResult> searchResults = new LinkedList<>();
        for (String contenderDocument : contenders) {
            double score = scorer.scoreDocumentByQuery(index, contenderDocument, query);
            SearchResult result = new SearchResult();
            result.document = contenderDocument;
            result.score = score;
            searchResults.add(result);
        }

        Collections.sort(searchResults);

        List<SearchResult> result = searchResults.stream().limit(maxNum).collect(Collectors.toList());
        int rank = 0;
        for (SearchResult sr : result) {
            sr.rank = rank;
            rank++;
        }
        return result;
    }
}
