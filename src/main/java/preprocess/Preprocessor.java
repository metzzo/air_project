package preprocess;

public class Preprocessor {
    private static Preprocessor ourInstance = new Preprocessor();

    public static Preprocessor getInstance() {
        return ourInstance;
    }

    private Preprocessor() {
    }

    public String[] preprocess(String input) {
        String caseFoldedInput = input.toLowerCase();

        return new String[] {caseFoldedInput};
    }
}
