import indexer.Indexer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class IndexerTest {

    @Test
    void indexFile() {
        // arrange
        ClassLoader classLoader = getClass().getClassLoader();
        URL res = classLoader.getResource("testdoc.txt");
        File file = new File(res.getFile());

        // act
        new Indexer().indexFile(file);

        // assert

    }
}