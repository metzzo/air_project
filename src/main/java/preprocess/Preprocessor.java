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
                    stopWords.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> preprocess(String input) {
        List<String> result = new LinkedList<>();
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(input));
        tokenizer.lowerCaseMode(true);
        try {
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                String val = tokenizer.sval;

                result.add(val);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }
}
