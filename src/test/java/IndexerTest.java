import indexer.Indexer;
import indexer.InvertedIndex;
import org.junit.jupiter.api.Test;
import preprocess.Preprocessor;

import java.io.File;
import java.net.URL;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class IndexerTest {
    @Test
    void indexSimpleFileWorking() {
        // arrange
        ClassLoader classLoader = getClass().getClassLoader();
        URL res = classLoader.getResource("simpletestdoc.txt");
        File file = new File(res.getFile());
        List<String> words = Preprocessor.getInstance().preprocess("hello my name is robert");
        InvertedIndex expectedIndex = new InvertedIndex();
        for (String word : words) {
            expectedIndex.putWord(word, new InvertedIndex.WordOccurence("LA010289-0001", 1));
        }

        // act
        InvertedIndex result = Indexer.getInstance().indexFile(file);

        // assert
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(expectedIndex));
    }


    @Test
    void indexLargeFileWorking() {
        // arrange
        ClassLoader classLoader = getClass().getClassLoader();
        URL res = classLoader.getResource("testdoc.txt");
        File file = new File(res.getFile());

        // act
        InvertedIndex result = Indexer.getInstance().indexFile(file);

        // assert
        assertThat(result, is(not(nullValue())));
        assertThat(result.getIndex().size(), is(greaterThan(10)));
        assertThat(result.getIndex(), hasKey("mother"));
        assertThat(result.getIndex(), hasKey("number"));
    }

    @Test
    void indexingWorking() {
        // arrange
        ClassLoader classLoader = getClass().getClassLoader();
        URL res1 = classLoader.getResource("testdoc.txt");
        File file1 = new File(res1.getFile());
        URL res2 = classLoader.getResource("testdoc2.txt");
        File file2 = new File(res2.getFile());
        List<File> files = Arrays.asList(file1, file2);

        // act
        InvertedIndex result = Indexer.getInstance().index(files, 8);

        // assert
        result.debugPrint();
        assertThat(result, is(not(nullValue())));
        assertThat(result.getIndex(), hasKey("mother"));
        assertThat(result.getIndex(), hasKey("number"));
        InvertedIndex.IndexValue val = result.getIndex().get("mother");
        assertThat(val.getDocuments(), hasKey("LA010289-0003"));
        assertThat(val.getDocuments(), hasKey("LA010289-0004"));
    }

    @Test
    void getFilesInDirWorking() {
        // arrange

        ClassLoader classLoader = getClass().getClassLoader();
        URL res = classLoader.getResource("testdir");
        File file = new File(res.getFile());
        Set<File> expectedFiles = new HashSet<>(Arrays.asList(
            new File(file.getAbsolutePath(), "testdir2\\testdir3\\testfile0"),
            new File(file.getAbsolutePath(), "testdir3\\hallo"),
            new File(file.getAbsolutePath(), "testfile1"),
            new File(file.getAbsolutePath(), "testfile2")
        ));

        // act
        List<File> files = Indexer.getInstance().getFilesInDir(file);

        // assert
        assertThat(new HashSet<>(files), is(expectedFiles));
    }
}