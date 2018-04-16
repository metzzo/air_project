package score;

import indexer.DocumentInfo;
import indexer.IndexValue;
import indexer.InvertedIndex;

public class ScoreUtility {
    private static ScoreUtility ourInstance = new ScoreUtility();

    public static ScoreUtility getInstance() {
        return ourInstance;
    }

    private ScoreUtility() {
    }

    public double tf(InvertedIndex index, DocumentInfo documentInfo, String word) {
        IndexValue indexValue = index.findByWord(word);
        double frequency = (double) indexValue.getFrequencyInDocument(documentInfo.getId());
        double maxFrequency = (double) documentInfo.getMaxFrequencyOfWord();
        return frequency / maxFrequency;
    }

    public double idf(InvertedIndex index, String word) {
        double numDocumentsContainingWord = index.findByWord(word).getNumDocuments();
        double numDocuments = (double) index.getDocumentRepository().getNumDocuments();
        return Math.log(numDocuments / numDocumentsContainingWord); // according to "An Introduction to IR" page 155
    }
}
