import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import preprocess.Preprocessor;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class PreprocessorTest {
    @BeforeEach
    void setUp() {
        Preprocessor.getInstance().setCaseFolding(false);
        Preprocessor.getInstance().setStemming(false);
        Preprocessor.getInstance().setStopWordRemovalEnabled(false);
        Preprocessor.getInstance().setLemmatizing(false);
    }

    @AfterEach
    void tearDown() {
        Preprocessor.getInstance().setCaseFolding(true);
        Preprocessor.getInstance().setStemming(true);
        Preprocessor.getInstance().setStopWordRemovalEnabled(true);
        Preprocessor.getInstance().setLemmatizing(false);
    }


    @Test
    void isDoingCaseFolding() {
        // arrange
        String input = "HelLo AdVanCed InfoRmaTioN RetRieval";
        List<String> expectedOutput = Arrays.asList("hello", "advanced", "information", "retrieval");

        // act
        Preprocessor.getInstance().setCaseFolding(true);
        List<String> actualOutput = Preprocessor.getInstance().preprocess(null, input);

        // assert

        assertThat(actualOutput, is(expectedOutput));
    }

    @Test
    void isDoingTokenization() {
        // arrange
        String input = "hello advanced information retrieval";
        List<String> expectedOutput = Arrays.asList("hello", "advanced", "information", "retrieval");

        // act
        List<String> actualOutput = Preprocessor.getInstance().preprocess(null, input);

        // assert
        assertThat(actualOutput, is(expectedOutput));
    }

    @Test
    void isDoingTokenizationWithApostrophe() {
        // arrange
        String input = "hello my's name´s is`s \"robert\"";
        List<String> expectedOutput = Arrays.asList("hello", "my", "name", "is", "robert");

        // act
        List<String> actualOutput = Preprocessor.getInstance().preprocess(null, input);

        // assert

        assertThat(actualOutput, is(expectedOutput));
    }

    @Test
    void isDoingTokenizationWithComma() {
        // arrange
        String input = "hello, advanced? information! - retrieval. . ( ) _ - ? !";
        List<String> expectedOutput = Arrays.asList("hello", "advanced", "information", "retrieval");

        // act

        List<String> actualOutput = Preprocessor.getInstance().preprocess(null, input);

        // assert
        assertThat(actualOutput, is(expectedOutput));
    }

    @Test
    void isDoingStopWordRemoval() {
        // arrange
        String input = "hello i am the advanced information retrieval";
        List<String> expectedOutput = Arrays.asList("hello", "advanced", "information", "retrieval");

        // act
        Preprocessor.getInstance().setStopWordRemovalEnabled(true);
        List<String> actualOutput = Preprocessor.getInstance().preprocess(null, input);

        // assert

        assertThat(actualOutput, is(expectedOutput));
    }

    @Test
    void isStemming() {
        // arrange
        String input = "hello advanced information retrieval";
        List<String> expectedOutput = Arrays.asList("hello", "advanc", "inform", "retriev");

        // act
        Preprocessor.getInstance().setStemming(true);
        List<String> actualOutput = Preprocessor.getInstance().preprocess(null, input);

        // assert

        assertThat(actualOutput, is(expectedOutput));
    }

    @Test
    void isLemmatizing() {
        // arrange
        String input = "hello advanced information retrieval";
        List<String> expectedOutput = Arrays.asList("hello", "advance", "information", "retrieval");

        // act
        Preprocessor.getInstance().setLemmatizing(true);
        List<String> actualOutput = Preprocessor.getInstance().preprocess(new StanfordCoreNLP(Preprocessor.stanfordNlpProperties()), input);

        // assert

        assertThat(actualOutput, is(expectedOutput));
    }
}