package preprocess;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.*;
import java.net.URL;
import java.util.*;

public class Preprocessor {
    private static final String STOP_WORDS = "stopwords.txt";
    private static Preprocessor ourInstance = new Preprocessor();

    private Set<String> stopWords;

    private boolean isStopWordRemovalEnabled = true;
    private boolean isCaseFolding = true;
    private boolean isStemming = true;
    private boolean isLemmatizing = false;

    public static Preprocessor getInstance() {
        return ourInstance;
    }

    private Preprocessor() {
        this.stopWords = new HashSet<>();

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream res = classLoader.getResourceAsStream(STOP_WORDS);
        if (res != null) {
            Scanner scanner = new Scanner(res);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim().toLowerCase();
                if (line.length() > 0) {
                    stopWords.add(line);
                }
            }
        } else {
            throw new RuntimeException("Could not load stopwords");
        }
    }

    public List<String> preprocess(StanfordCoreNLP pipeline, String input) {
        if (isCaseFolding) {
            input = input.toLowerCase();
        }
        input = input.replaceAll("(('|´|`)[^\\s]+)", "");

        int currentPosition = 0;
        List<String> result = new LinkedList<>();
        StringBuilder currentToken = new StringBuilder();
        while (currentPosition < input.length()) {
            char cur = input.charAt(currentPosition);
            if (    cur <= '\u0020' || // whitespace
                    cur == '.' ||
                    cur == ':' ||
                    cur == ',' ||
                    cur == '?'  ||
                    cur == '"' ||
                    cur == '\'' ||
                    cur == '!' ||
                    cur == '(' ||
                    cur == ')' ||
                    cur == '-' ||
                    cur == '_' ||
                    cur == '&' ||
                    cur == ';') {
                addToken(pipeline, result, currentToken.toString());
                currentToken = new StringBuilder();

            } else {
                currentToken.append(cur);
            }
            currentPosition++;
        }
        addToken(pipeline, result, currentToken.toString());
        return result;
    }

    private void addToken(StanfordCoreNLP pipeline, List<String> tokens, String val) {
        boolean isStopword = this.isStopWordRemovalEnabled && stopWords.contains(val.toLowerCase());

        if (!isStopword && val.length() > 0) {
            if (isStemming) {
                // weird Java Class is weird
                Stemmer s = new Stemmer();
                s.add(val.toCharArray(), val.length());
                s.stem();
                val = s.toString();
            }

            if (isLemmatizing) {
                Annotation tokenAnnotation = new Annotation(val);
                pipeline.annotate(tokenAnnotation);  // necessary for the LemmaAnnotation to be set.
                List<CoreMap> list = tokenAnnotation.get(CoreAnnotations.SentencesAnnotation.class);
                val = list
                        .get(0).get(CoreAnnotations.TokensAnnotation.class)
                        .get(0).get(CoreAnnotations.LemmaAnnotation.class);
            }

            if (isLemmatizing && isStemming) {
                throw new RuntimeException("Cannot lemmatize AND stemm");
            }

            tokens.add(val);
        }
    }

    public void setStemming(boolean stemming) {
        isStemming = stemming;
    }

    public boolean isStemming() {
        return isStemming;
    }

    public void setLemmatizing(boolean lemmatizing) {
        isLemmatizing = lemmatizing;
    }

    public boolean isLemmatizing() {
        return isLemmatizing;
    }

    public void setCaseFolding(boolean caseFolding) {
        isCaseFolding = caseFolding;
    }

    public boolean isCaseFolding() {
        return isCaseFolding;
    }

    public void setStopWordRemovalEnabled(boolean stopWordRemovalEnabled) {
        isStopWordRemovalEnabled = stopWordRemovalEnabled;
    }

    public boolean isStopWordRemovalEnabled() {
        return isStopWordRemovalEnabled;
    }

    public static Properties stanfordNlpProperties() {
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        return props;
    }
}
