package preprocess;

import indexer.Indexer;
import indexer.InvertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

public class TopicExtractor {
    private static TopicExtractor instance = new TopicExtractor();

    public static TopicExtractor getInstance() {
        return instance;
    }

    private TopicExtractor() {

    }

    public List<Topic> extract(File file, int startIndex) {
        if (!file.exists()) {
            throw new RuntimeException("File to index does not exist");
        }
        int number = startIndex;
        List<Topic> topics = new LinkedList<>();
        try (FileInputStream fstream = new FileInputStream(file)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Charset.forName("ISO8859-1")));

            String strLine;
            Topic topic = null;
            StringBuilder content =  null;
            while ((strLine = br.readLine()) != null)   {
                int currentPos = 0;
                while (true) {
                    int startPos = strLine.indexOf('<', currentPos);
                    if (startPos == -1) {
                        break;
                    }
                    int endPos = strLine.indexOf('>', startPos);
                    if (content != null) {
                        content.append(strLine, currentPos, startPos);
                    }

                    String text = strLine.substring(startPos + 1, endPos).toUpperCase();
                    if (text.equals("NUM")) {
                        topic.setNumber(number);
                        number++;

                        endPos = strLine.length();
                    } else if (text.equals("TITLE") || text.equals("DESC") || text.equals("NARR")) {
                        if (content == null) {
                            content = new StringBuilder();
                        }
                        endPos = strLine.length();
                    } else if (text.equals("TOP")) {
                        topic = new Topic();
                    } else if (text.equals("/TOP")) {
                        topic.setQuery(content.toString());
                        topics.add(topic);
                        content = null;
                    }

                    currentPos = endPos + 1;
                }
                if (content != null && currentPos < strLine.length()) {
                    content.append(strLine, currentPos, strLine.length());
                    content.append('\n');
                }
            }
            br.close();
            return topics;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
