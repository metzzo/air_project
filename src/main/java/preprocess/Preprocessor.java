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
        }
    }

    public List<String> preprocess(String input) {
        List<String> result = new LinkedList<>();
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(input));
        tokenizer.lowerCaseMode(isCaseFolding);
        try {
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                String val = tokenizer.sval;

                if (val != null) {
                    val = val.replaceAll("\\.", "");
                }

                boolean isStopword = this.isStopWordRemovalEnabled && stopWords.contains(val);

                if (val != null && !isStopword) {

                    if (isStemming) {
                        // weird Java Class is weird
                        Stemmer s = new Stemmer();
                        s.add(val.toCharArray(), val.length());
                        s.stem();
                        val = s.toString();
                    }

                    result.add(val);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return result;
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
