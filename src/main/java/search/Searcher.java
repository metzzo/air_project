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
        DocumentRepository tmp = new DocumentRepository();
        InvertedIndex queryIndex = Indexer.getInstance().indexString(this.pipeline, tmp, "query", query);
        DocumentInfo queryDoc = tmp.getDocumentByName("query");

        Map<Integer, Integer> contenders = new HashMap<>();
        for (String word : queryIndex.getWords()) {
            if (index.containsWord(word)) {
                IndexValue val = index.findByWord(word);
                for (Integer doc : val.getAllDocuments()) {
                    if (contenders.containsKey(doc)) {
                        int num = contenders.get(doc);
                        contenders.put(doc, num + 1);
                    } else {
                        contenders.put(doc, 1);
                    }
                }
            }
        }

        // average frequency
        double avgContained = 0.0;
        for (Integer contained : contenders.values()) {
            avgContained += contained;
        }
        avgContained /= contenders.size();

        List<SearchResult> searchResults = new LinkedList<>();
        for (Integer contenderDocument : contenders.keySet()) {
            Integer contained = contenders.get(contenderDocument);
            if (contained >= avgContained * 0.25) {
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
