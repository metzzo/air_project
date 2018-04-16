import org.junit.jupiter.api.Test;
import preprocess.Topic;
import preprocess.TopicExtractor;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


class TopicExtractorTest {
    @Test
    void topicExtractionWorks() {
        // arrange
        ClassLoader classLoader = getClass().getClassLoader();
        URL res = classLoader.getResource("topics.txt");
        File file = new File(res.getFile());

        // act
        List<Topic> topics = TopicExtractor.getInstance().extract(file, 401);

        // assert
        assertThat(topics.size(), is(50));
        assertThat(topics.get(10).getNumber(), is (401 + 10));
        assertThat(topics.get(10).getQuery(), is("Find information on shipwreck salvaging: the\nrecovery or attempted recovery of treasure from\nsunken ships.\nA relevant document will provide information on\nthe actual locating and recovery of treasure; \non the technology which makes possible the discovery,\nlocation and investigation of wreckages which \ncontain or are suspected of containing treasure; or\non the disposition of the recovered treasure. \n"));
    }
}