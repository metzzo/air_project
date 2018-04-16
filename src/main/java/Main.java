import indexer.DocumentRepository;
import indexer.Indexer;
import indexer.InvertedIndex;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    enum Action {
        INDEX,
        QUERY
    }
    private static final File dataFile = new File("inverted_index.data");
    private static final File xmlFile = new File("document_repository.xml");

    public static void main(String[] args) {
        Action action = Action.QUERY;
        String folder = "";
        int threadCount = 1;
        for (int i = 0; i < args.length; i += 2) {
            String arg = args[i];
            String param = args[i + 1];

            switch(arg) {
                case "--action":
                    switch (param) {
                        case "index":
                            action = Action.INDEX;
                            break;
                        case "query":
                            action = Action.QUERY;
                            break;
                        default:
                            throw new RuntimeException("Unknown action");
                    }
                    break;
                case "--folder":
                    folder = param;
                    break;
                case "--threadcount":
                    threadCount = Integer.valueOf(param);
                    break;
            }

        }

        switch(action) {
            case INDEX:
                doIndex(folder, threadCount);
                break;
            case QUERY:
                doQuery();
                break;
        }
    }

    private static void doQuery() {
        System.out.println("Loading...");
        InvertedIndex index;
        try {
            DocumentRepository repo = DocumentRepository.deserialize(new FileInputStream("document_repository.txt"));
            index = InvertedIndex.deserialize(repo, new FileInputStream("inverted_index.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Ready");
    }

    private static void doIndex(String folderStr, int threadCount) {
        try {
            File folder = new File(folderStr);
            List<File> files = Indexer.getInstance().getFilesInDir(folder);
            DocumentRepository repo = new DocumentRepository();
            InvertedIndex index = Indexer.getInstance().index(repo, files, threadCount); // Arrays.asList(files.get(0))

            System.out.println("Saving Index...");
            if (dataFile.exists()) {
                dataFile.delete();
            }
            index.serialize(new FileOutputStream(dataFile));

            System.out.println("Saving Document Repository...");
            if (xmlFile.exists()) {
                xmlFile.delete();
            }
            repo.serialize(new FileOutputStream(xmlFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
