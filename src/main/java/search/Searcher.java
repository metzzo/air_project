package search;

import indexer.IndexValue;
import indexer.InvertedIndex;
import score.CosineSimilarity;
import score.Scorer;

import java.util.*;
import java.util.stream.Collectors;

public class Searcher {

    private static Searcher searcher = new Searcher();

    public static Searcher getInstance() {
        return searcher;
    }

    private Searcher() { }


    public List<SearchResult> search(InvertedIndex index, List<String> query, Scorer scorer, int maxNum) {
        // get candidate documents by chosing documents that contain at least 1 term of the query
        Set<Integer> contenders = new HashSet<>();
        for (String word : query) {
            IndexValue val = index.findByWord(word);
            contenders.addAll(val.getAllDocuments());
        }

        // TODO: use cosine similarity

        List<SearchResult> searchResults = new LinkedList<>();
        for (Integer contenderDocument : contenders) {
            double score = CosineSimilarity.getInstance().scoreDocumentByQuery(
                    index,
                    index.getDocumentRepository().getDocumentById(contenderDocument),
                    query,
                    scorer
            );
            SearchResult result = new SearchResult(contenderDocument, score);
            searchResults.add(result);
        }

        Collections.sort(searchResults);

        List<SearchResult> result = searchResults.stream().limit(maxNum).collect(Collectors.toList());
        int rank = 0;
        for (SearchResult sr : result) {
            sr.setRank(rank);
            rank++;
        }
        return result;
    }
}
