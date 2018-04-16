import indexer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import preprocess.Preprocessor;

import java.io.File;
import java.net.URL;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class IndexerTest {
    private DocumentRepository repo;

    @BeforeEach
    void setUp() {
        repo = new DocumentRepository();
    }

    @Test
    void indexSimpleFileWorking() {
        // arrange
        DocumentInfo info = repo.register("LA010289-0001", 0);

        ClassLoader classLoader = getClass().getClassLoader();
        URL res = classLoader.getResource("simpletestdoc.txt");
        File file = new File(res.getFile());
        List<String> words = Preprocessor.getInstance().preprocess("hello my name is robert robert");
        InvertedIndex expectedIndex = new InvertedIndex(repo);
        for (String word : words) {
            expectedIndex.putWord(word, new WordOccurence(info.getId(), 1));
        }

        // act
        InvertedIndex result = Indexer.getInstance().indexFile(repo, file);

        // assert
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(expectedIndex));
        assertThat(info.getSize(), is(5));
        assertThat(info.getMaxFrequencyOfWord(), is(2));
    }


    @Test
    void indexLargeFileWorking() {
        // arrange
        DocumentInfo info = repo.register("LA010289-0001", 0);

        ClassLoader classLoader = getClass().getClassLoader();
        URL res = classLoader.getResource("testdoc.txt");
        File file = new File(res.getFile());

        // act
        InvertedIndex result = Indexer.getInstance().indexFile(repo, file);

        // assert
        assertThat(result, is(not(nullValue())));
        assertThat(result.getNumWords(), is(greaterThan(10)));
        assertThat(result.containsWord("mother"), is(true));
        assertThat(result.containsWord("number"), is (true));
        assertThat(info.getSize(), is(greaterThan(100)));
        assertThat(info.getSize(), is(267));
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
        InvertedIndex result = Indexer.getInstance().index(repo, files, 8);

        // assert
        result.debugPrint();
        assertThat(result, is(not(nullValue())));
        assertThat(result.containsWord("mother"), is(true));
        assertThat(result.containsWord("number"), is (true));
        IndexValue val = result.findByWord("mother");
        assertThat(val.isInDocument(repo.getDocumentByName("LA010289-0003").getId()), is(true));
        assertThat(val.isInDocument(repo.getDocumentByName("LA010289-0004").getId()), is(true));
        assertThat(repo.getDocumentByName("LA010289-0001").getSize(), is(greaterThan(100)));
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