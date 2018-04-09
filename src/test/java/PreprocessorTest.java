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
        List<String> actualOutput = Preprocessor.getInstance().preprocess(input);

        // assert

        assertThat(actualOutput, is(expectedOutput));
    }
}