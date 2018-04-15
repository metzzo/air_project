package preprocess;

import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.URL;
import java.util.*;

public class Preprocessor {
    private static final String STOP_WORDS = "stopwords.txt";
    private static Preprocessor ourInstance = new Preprocessor();

    private Set<String> stopWords;

    private boolean isStopWordRemovalEnabled = true;
    private boolean isCaseFolding = true;
    private boolean isStemming = true;
    private int minLength = 2;

    public static Preprocessor getInstance() {
        return ourInstance;
    }

    private Preprocessor() {
        this.stopWords = new HashSet<>();

        ClassLoader classLoader = getClass().getClassLoader();
        URL res = classLoader.getResource(STOP_WORDS);
        if (res != null) {
            File file = new File(res.getFile());

            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim().toLowerCase();
                    if (line.length() > 0) {
                        stopWords.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("Could not load stopwords");
        }
    }

    public List<String> preprocess(String input) {
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
                    cur == '&' ||
                    cur == ';') {
                addToken(result, currentToken.toString());
                currentToken = new StringBuilder();

            } else {
                currentToken.append(cur);
            }
            currentPosition++;
        }
        addToken(result, currentToken.toString());
        return result;
    }

    private void addToken(List<String> tokens, String val) {
        boolean isStopword = this.isStopWordRemovalEnabled && stopWords.contains(val);

        if (!isStopword && val.length() > 0) {
            if (isStemming) {
                // weird Java Class is weird
                Stemmer s = new Stemmer();
                s.add(val.toCharArray(), val.length());
                s.stem();
                val = s.toString();
            }

            if (val.length() >= minLength) {
                tokens.add(val);
            }
        }
    }

    public void setStemming(boolean stemming) {
        isStemming = stemming;
    }

    public boolean isStemming() {
        return isStemming;
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
}
