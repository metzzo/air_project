package search;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import indexer.*;
import preprocess.Preprocessor;
import score.CosineSimilarity;
import score.ScoreCalculator;
import score.SimilarityCalculator;

import java.util.*;
import java.util.stream.Collectors;

public class Searcher {

    private static Searcher searcher = new Searcher();
    private final StanfordCoreNLP pipeline;

    public static Searcher getInstance() {
        return searcher;
    }

    private Searcher() {
        this.pipeline = new StanfordCoreNLP(Preprocessor.stanfordNlpProperties());
    }


    public List<SearchResult> search(InvertedIndex index, String query, ScoreCalculator scorer, SimilarityCalculator similarity, int maxNum) {
        // get candidate documents by chosing documents that contain at least 1 term of the query
        // create document out of query
        InvertedIndex queryIndex = Indexer.getInstance().indexString(this.pipeline, index.getDocumentRepository(), "query", query);
        DocumentInfo queryDoc = index.getDocumentRepository().getDocumentByName("query");

        Set<Integer> contenders = new HashSet<>();
        for (String word : queryIndex.getWords()) {
            IndexValue val = index.findByWord(word);
            contenders.addAll(val.getAllDocuments());
        }

        List<SearchResult> searchResults = new LinkedList<>();
        for (Integer contenderDocument : contenders) {
            double score = similarity.similarityToQuery(
                    index,
                    queryIndex,
                    index.getDocumentRepository().getDocumentById(contenderDocument),
                    queryDoc,
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
