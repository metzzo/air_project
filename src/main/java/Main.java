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
    enum ScoreFunctionType {
        TFIDF,
        BM25,
        BM25VA
    }
    enum Score {
        COSINE,
        SUM
    }

    public static void main(String[] args) {
        Action action = Action.QUERY;
        ScoreFunctionType scoreFunction = ScoreFunctionType.TFIDF;
        Score score = Score.COSINE;
        String folder = "";
        int threadCount = 1;
        int startIndex = 401;
        int numResults = 1000;
        double k1 = 1.5;
        double b = 0.75;
        String topicfile = "", outputfile = "";
        String indexfile = "inverted_index.data";
        String documentrepofile = "document_repository.data";

        Preprocessor.getInstance().setLemmatizing(true);
        Preprocessor.getInstance().setStemming(false);
        Preprocessor.getInstance().setStopWordRemovalEnabled(true);
        Preprocessor.getInstance().setCaseFolding(true);

        for (int i = 0; i < args.length; i += 2) {
            String arg = args[i];
            String param = args[i + 1];

            switch(arg.toLowerCase()) {
                case "--action":
                    switch (param.toLowerCase()) {
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
                case "--scorefunction":
                    switch (param.toLowerCase()) {
                        case "tfidf":
                            scoreFunction = ScoreFunctionType.TFIDF;
                            break;
                        case "bm25":
                            scoreFunction = ScoreFunctionType.BM25;
                            break;
                        case "bm25va":
                            scoreFunction = ScoreFunctionType.BM25VA;
                            break;
                        default:
                            throw new RuntimeException("Unknown score function");
                    }
                    break;
                case "--score":
                    switch (param) {
                        case "sum":
                            score = Score.SUM;
                            break;
                        case "cosine":
                            score = Score.COSINE;
                            break;
                        default:
                            throw new RuntimeException("Unknown score");
                    }
                    break;
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
                case "--outputfile":
                    outputfile = param;
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
                case "--indexfile":
                    indexfile = param;
                    break;
                case "--documentrepofile":
                    documentrepofile = param;
                    break;
                default:
                    throw new RuntimeException("Unknown Parameter");
            }

        }

        switch(action) {
            case INDEX:
                doIndex(indexfile, documentrepofile, folder, threadCount);
                break;
            case QUERY:
                doQuery(indexfile, documentrepofile, topicfile, outputfile, startIndex, scoreFunction, numResults, k1, b, score);
                break;
        }
    }

    private static void doQuery(String indexfile, String documentrepofile, String file, String outputfile, int startIndex, ScoreFunctionType scoreFunctionType, int numResults, double k1, double b, Score score) {
        System.out.println("Loading Topics...");
        List<Topic> topics = TopicExtractor.getInstance().extract(new File(file), startIndex);

        System.out.println("Loading Index...");
        InvertedIndex index;
        try {
            DocumentRepository repo = DocumentRepository.deserialize(new FileInputStream(new File(documentrepofile)));
            index = InvertedIndex.deserialize(repo, new FileInputStream(new File(indexfile)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        PrintStream out;
        if (outputfile.length() > 0) {
            try {
                out = new PrintStream(outputfile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            out = System.out;
        }

        System.out.println("Querying...");
        ScoreCalculator similarity = null;
        switch (score) {
            case SUM:
                similarity = new SumScore();
                break;
            case COSINE:
                similarity = new CosineScore();
                break;
        }
        ScoreFunction scoreFunction = null;
        switch(scoreFunctionType) {
            case TFIDF:
                scoreFunction = new TFIDFScore();
                break;
            case BM25:
                scoreFunction = new BM25Score(k1, b);
                break;
            case BM25VA:
                scoreFunction = new BM25VAScore(k1);
                break;
        }
        for (Topic topic : topics) {
            List<SearchResult> results = Searcher.getInstance().search(index, topic.getQuery(), scoreFunction, similarity, numResults);
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
                out.println(str);
            }
        }
        System.out.println("Done...");
    }

    private static void doIndex(String indexstr, String documentrepostr, String folderStr, int threadCount) {
        try {
            File indexFile= new File(indexstr);
            File documentRepoFile = new File(documentrepostr);
            File folder = new File(folderStr);
            List<File> files = Indexer.getInstance().getFilesInDir(folder);
            InvertedIndex index = Indexer.getInstance().index(files, threadCount); // Arrays.asList(files.get(0), files.get(1), files.get(2), files.get(3))

            System.out.println("Saving Index...");
            if (indexFile.exists()) {
                indexFile.delete();
            }
            index.serialize(new FileOutputStream(indexFile));

            System.out.println("Saving Document Repository...");
            if (documentRepoFile.exists()) {
                documentRepoFile.delete();
            }
            index.getDocumentRepository().serialize(new FileOutputStream(documentRepoFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
