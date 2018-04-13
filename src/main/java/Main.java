import indexer.Indexer;
import indexer.InvertedIndex;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length <= 0) {
            System.err.println("Missing path");
            return;
        }
        File folder = new File(args[0]);
        List<File> files = Indexer.getInstance().getFilesInDir(folder);
        InvertedIndex index = Indexer.getInstance().index(files, 1);

    }
}
