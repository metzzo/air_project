import org.junit.jupiter.api.Test;
import preprocess.Preprocessor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.jupiter.api.Assertions.*;

class PreprocessorTest {
    @Test
    void isDoingCaseFolding() {
        // arrange
        String input = "HaLlO";
        String[] expectedOutput = new String[] {"hallo"};

        // act
        String[] actualOutput = Preprocessor.getInstance().preprocess(input);

        // assert

        assertThat(actualOutput, is(expectedOutput));
    }
}