import org.junit.jupiter.api.Test;
import preprocess.Preprocessor;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.jupiter.api.Assertions.*;

class PreprocessorTest {
    @Test
    void isDoingCaseFolding() {
        // arrange
        String input = "HelLo AdVanCed InfoRmaTioN RetRieval";
        List<String> expectedOutput = Arrays.asList("hello", "advanced", "information", "retrieval");

        // act
        Preprocessor.getInstance().setCaseFolding(true);
        Preprocessor.getInstance().setStemming(false);
        Preprocessor.getInstance().setStopWordRemovalEnabled(false);

        List<String> actualOutput = Preprocessor.getInstance().preprocess(input);

        // assert

        assertThat(actualOutput, is(expectedOutput));
    }

    @Test
    void isDoingTokenization() {
        // arrange
        String input = "hello advanced information retrieval";
        List<String> expectedOutput = Arrays.asList("hello", "advanced", "information", "retrieval");

        // act
        Preprocessor.getInstance().setCaseFolding(false);
        Preprocessor.getInstance().setStemming(false);
        Preprocessor.getInstance().setStopWordRemovalEnabled(false);
        List<String> actualOutput = Preprocessor.getInstance().preprocess(input);

        // assert

        assertThat(actualOutput, is(expectedOutput));
    }

    @Test
    void isDoingTokenizationWithComma() {
        // arrange
        String input = "hello, advanced information - retrieval";
        List<String> expectedOutput = Arrays.asList("hello", "advanced", "information", "retrieval");

        // act
        Preprocessor.getInstance().setCaseFolding(false);
        Preprocessor.getInstance().setStemming(false);
        Preprocessor.getInstance().setStopWordRemovalEnabled(false);
        List<String> actualOutput = Preprocessor.getInstance().preprocess(input);

        // assert

        assertThat(actualOutput, is(expectedOutput));
    }

    @Test
    void isDoingStopWordRemoval() {
        // arrange
        String input = "hello i am the advanced information retrieval";
        List<String> expectedOutput = Arrays.asList("hello", "advanced", "information", "retrieval");

        // act

        Preprocessor.getInstance().setCaseFolding(false);
        Preprocessor.getInstance().setStemming(false);
        Preprocessor.getInstance().setStopWordRemovalEnabled(true);
        List<String> actualOutput = Preprocessor.getInstance().preprocess(input);

        // assert

        assertThat(actualOutput, is(expectedOutput));
    }

    @Test
    void isStemming() {
        // arrange
        String input = "hello advanced information retrieval";
        List<String> expectedOutput = Arrays.asList("hello", "advanc", "inform", "retriev");

        // act
        Preprocessor.getInstance().setCaseFolding(false);
        Preprocessor.getInstance().setStemming(true);
        Preprocessor.getInstance().setStopWordRemovalEnabled(false);
        List<String> actualOutput = Preprocessor.getInstance().preprocess(input);

        // assert

        assertThat(actualOutput, is(expectedOutput));
    }
}