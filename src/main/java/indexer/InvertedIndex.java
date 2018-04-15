package indexer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@XmlRootElement(name = "inverted-index")
@XmlAccessorType(XmlAccessType.NONE)
public class InvertedIndex {
    @XmlElement(name = "index")
    private Map<String, IndexValue> index;
    @XmlElement(name = "max_words_per_document")
    private Map<String, Integer> maxWordsPerDocument;

    public InvertedIndex() {
        this.index = new HashMap<>();
        this.maxWordsPerDocument = new HashMap<>();
    }

    public void putWord(String word, WordOccurence wordOccurence) {
        if (!this.index.containsKey(word)) {
            this.index.put(word, new IndexValue(wordOccurence));
        } else {
            IndexValue val = this.index.get(word);
            val.putWord(wordOccurence);
        }

        if (!this.maxWordsPerDocument.containsKey(wordOccurence.document)) {
            this.maxWordsPerDocument.put(wordOccurence.document, 0);
        }

        IndexValue val = this.index.get(word);
        int count = val.getFrequencyInDocument(wordOccurence.document);
        int currentMaxCount = this.maxWordsPerDocument.get(wordOccurence.document);
        if (count > currentMaxCount) {
            this.maxWordsPerDocument.put(wordOccurence.document, count);
        }
    }

    public void merge(InvertedIndex other) {
        for (String word : other.index.keySet()) {
            IndexValue value = other.index.get(word);
            Map<String, Integer> documents = value.getFrequenciesForDocuments();
            for (String document : documents.keySet()) {
                int count = documents.get(document);
                this.putWord(word, new WordOccurence(document, count));
            }
        }
    }

    public IndexValue findByWord(String word) {
        return this.index.get(word);
    }

    public boolean containsWord(String word) {
        return this.index.containsKey(word);
    }

    public int getMaxFrequencyInDocument(String document) {
        return this.maxWordsPerDocument.get(document);
    }

    public int getNumDocuments() {
        return this.maxWordsPerDocument.size();
    }

    public int getNumWords() {
        return this.index.size();
    }

    public void debugPrint() {
        for (String word : this.index.keySet()) {
            IndexValue value = this.index.get(word);
            Map<String, Integer> documents = value.getFrequenciesForDocuments();
            System.out.print(word + ": ");
            for (String document : documents.keySet()) {
                int count = documents.get(document);
                System.out.print(document + "(" + count + ");");
            }
            System.out.println();
        }
        System.out.println();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InvertedIndex) || getClass() != obj.getClass()) {
            return false;
        }
        InvertedIndex other = (InvertedIndex)obj;

        for (String word : this.index.keySet()) {
            IndexValue myValue = this.index.get(word);
            IndexValue otherValue = other.index.get(word);
            if (!myValue.equals(otherValue)) {
                return false;
            }
        }

        for (String word : other.index.keySet()) {
            IndexValue myValue = this.index.get(word);
            IndexValue otherValue = other.index.get(word);
            if (!myValue.equals(otherValue)) {
                return false;
            }
        }

        for (String doc : this.maxWordsPerDocument.keySet()) {
            int myCount = this.maxWordsPerDocument.get(doc);
            int otherCount = other.maxWordsPerDocument.get(doc);
            if (myCount != otherCount) {
                return false;
            }
        }

        for (String doc : other.maxWordsPerDocument.keySet()) {
            int myCount = this.maxWordsPerDocument.get(doc);
            int otherCount = other.maxWordsPerDocument.get(doc);
            if (myCount != otherCount) {
                return false;
            }
        }

        return true;
    }

    public void serialize(OutputStream stream) {
        try {
            JAXBContext jc = JAXBContext.newInstance(InvertedIndex.class);
            Marshaller marshaller = jc.createMarshaller();
            //marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this, stream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static InvertedIndex deserialize(InputStream stream) {
        try {
            JAXBContext jc = JAXBContext.newInstance(InvertedIndex.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            //marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            return (InvertedIndex)unmarshaller.unmarshal(stream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
