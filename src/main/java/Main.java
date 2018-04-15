import indexer.Indexer;
import indexer.InvertedIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

public class Main {
    enum Action {
        INDEX,
        QUERY
    }
    public static void main(String[] args) {
        Action action = Action.QUERY;
        String folder = "";
        for (int i = 0; i < args.length; i += 2) {
            String arg = args[i];
            String param = args[i + 1];

            switch(arg) {
                case "action":
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
                case "folder":
                    folder = param;
                    break;
            }

        }

        switch(action) {
            case INDEX:
                doIndex(folder);
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
            index = InvertedIndex.deserialize(new FileInputStream("inverted_index.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Ready");
    }

    private static void doIndex(String folderStr) {
        File folder = new File(folderStr);
        List<File> files = Indexer.getInstance().getFilesInDir(folder);

        InvertedIndex index = Indexer.getInstance().index(files, 16); // Arrays.asList(files.get(0))

        System.out.println("Saving...");
        try {
            index.serialize(new FileOutputStream("inverted_index.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
