import indexer.DocumentRepository;
import indexer.Indexer;
import indexer.InvertedIndex;
import preprocess.Preprocessor;
import preprocess.Topic;
import preprocess.TopicExtractor;
import score.*;
import search.SearchResult;
import search.Searcher;

import java.io.*;
import java.util.List;

public class Main {
    enum Action {
        INDEX,
        QUERY
    }
    enum Score {
        TFIDF,
        BM25,
        BM25VA
    }
    private static final File dataFile = new File("inverted_index.data");
    private static final File xmlFile = new File("document_repository.xml");

    public static void main(String[] args) {
        Action action = Action.QUERY;
        Score score = Score.TFIDF;
        String folder = "";
        int threadCount = 1;
        int startIndex = 401;
        int numResults = 1000;
        double k1 = 1.5;
        double b = 0.75;
        String topicfile = "";

        Preprocessor.getInstance().setLemmatizing(true);
        Preprocessor.getInstance().setStemming(false);
        Preprocessor.getInstance().setStopWordRemovalEnabled(true);
        Preprocessor.getInstance().setCaseFolding(true);

        for (int i = 0; i < args.length; i += 2) {
            String arg = args[i];
            String param = args[i + 1];

            switch(arg.toLowerCase()) {
                case "--action":
                    switch (param) {
                        case "index":
                            action = Action.INDEX;
                            break;
                        case "query":
                            action = Action.QUERY;
                            break;
                        default:
                            throw new RuntimeException("Unknown action");
                    }
                    break;
                case "--score":
                    switch (param) {
                        case "tfidf":
                            score = Score.TFIDF;
                            break;
                        case "bm25":
                            score = Score.BM25;
                            break;
                        case "bm25va":
                            score = Score.BM25VA;
                            break;
                        default:
                            throw new RuntimeException("Unknown score");
                    }
                case "--folder":
                    folder = param;
                    break;
                case "--threadcount":
                    threadCount = Integer.valueOf(param);
                    break;
                case "--startindex":
                    startIndex = Integer.valueOf(param);
                    break;
                case "--topicfile":
                    topicfile = param;
                    break;
                case "--numresults":
                    numResults = Integer.valueOf(param);
                    break;
                case "--b":
                    b = Double.valueOf(param);
                    break;
                case "--k1":
                    k1 = Double.valueOf(param);
                    break;
                case "--stopwordremoval": {
                    Boolean enable = Boolean.valueOf(param);
                    Preprocessor.getInstance().setStopWordRemovalEnabled(enable);
                    break;
                }
                case "--casefolding": {
                    Boolean enable = Boolean.valueOf(param);
                    Preprocessor.getInstance().setCaseFolding(enable);
                    break;
                }
                case "--stemming": {
                    Boolean enable = Boolean.valueOf(param);
                    Preprocessor.getInstance().setStemming(enable);
                    break;
                }
                case "--lemmatize": {
                    Boolean enable = Boolean.valueOf(param);
                    Preprocessor.getInstance().setLemmatizing(enable);
                    break;
                }
                default:
                    throw new RuntimeException("Unknown Parameter");
            }

        }

        switch(action) {
            case INDEX:
                doIndex(folder, threadCount);
                break;
            case QUERY:
                doQuery(topicfile, startIndex, score, numResults, k1, b);
                break;
        }
    }

    private static void doQuery(String file, int startIndex, Score scorerType, int numResults, double k1, double b) {
        System.out.println("Loading Topics...");
        List<Topic> topics = TopicExtractor.getInstance().extract(new File(file), startIndex);

        System.out.println("Loading Index...");
        InvertedIndex index;
        try {
            DocumentRepository repo = DocumentRepository.deserialize(new FileInputStream(xmlFile));
            index = InvertedIndex.deserialize(repo, new FileInputStream(dataFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Querying...");
        SimilarityCalculator similarity = new CosineSimilarity();
        ScoreCalculator scorer = null;
        switch(scorerType) {
            case TFIDF:
                scorer = new TFIDFScore();
                break;
            case BM25:
                scorer = new BM25Score(k1, b);
                break;
            case BM25VA:
                scorer = new BM25VAScore(k1);
                break;
        }
        for (Topic topic : topics) {
            List<SearchResult> results = Searcher.getInstance().search(index, topic.getQuery(), scorer, similarity, numResults);
            for (SearchResult result : results) {
                StringBuilder str = new StringBuilder();
                str.append(topic.getNumber());
                str.append(" Q0 ");
                str.append(index.getDocumentRepository().getDocumentById(result.getDocumentId()).getName());
                str.append(" ");
                str.append(result.getRank());
                str.append(" ");
                str.append(result.getScore());
                str.append(" testrun");
                System.out.println(str);
            }
        }
    }

    private static void doIndex(String folderStr, int threadCount) {
        try {
            File folder = new File(folderStr);
            List<File> files = Indexer.getInstance().getFilesInDir(folder);
            InvertedIndex index = Indexer.getInstance().index(files, threadCount); // Arrays.asList(files.get(0))

            System.out.println("Saving Index...");
            if (dataFile.exists()) {
                dataFile.delete();
            }
            index.serialize(new FileOutputStream(dataFile));

            System.out.println("Saving Document Repository...");
            if (xmlFile.exists()) {
                xmlFile.delete();
            }
            index.getDocumentRepository().serialize(new FileOutputStream(xmlFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
