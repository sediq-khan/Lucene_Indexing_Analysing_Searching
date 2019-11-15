import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    String userInputDataDir = "";
    List<File> filesFromUserInputDir = null;
    String searchItem = "";
    String userInputIndexDir = "";
    LuceneOperations luceneOperations;
    Searcher searcher;

    public static void main(String[] args) throws IOException, ParseException {
        Main mainObject = new Main();

        System.out.println("Enter following:- 1. Data Directory, 2. Search Item, 3. Index Directory");

        if (args.length < 3){
            System.out.println("Please enter atleast the three said arguments.");
            return;
        }
        mainObject.searchItem = args[1].toLowerCase().trim();
        mainObject.userInputIndexDir = args[2].toLowerCase().trim().toString();
        mainObject.userInputDataDir = args[0].trim().toLowerCase();

        mainObject.createFileList();
        mainObject.createIndex();
        mainObject.search(mainObject.searchItem);
    }

    public void createFileList(){
        try {
            Path filePath = new File(userInputDataDir).toPath();
            boolean exists =      Files.exists(filePath);        // Check if the file exists
            boolean isDirectory = Files.isDirectory(filePath);   // Check if it's a directory
            boolean isFile =      Files.isRegularFile(filePath); // Check if it's a regular file
            System.out.println("The system will read the files from all directories and sub-directories of the path you just provided.");
            filesFromUserInputDir = fileExtensionLookup.fileExtensionLookup(userInputDataDir);
        }catch (Exception ex){
            System.out.println("<<< Please enter a valid directory or a file path. >>>");
        }
    }

    public void createIndex() throws IOException {
        luceneOperations = new LuceneOperations(userInputIndexDir);
        int numIndexed;
        long startTime = System.currentTimeMillis();
        numIndexed = luceneOperations.createIndex(filesFromUserInputDir);
        long endTime = System.currentTimeMillis();
        luceneOperations.close();
        System.out.println(numIndexed + " Files Indexed, time taken: " + (endTime - startTime) + " ms");

    }

    private void search(String searchQuery) throws IOException, ParseException {
        searcher = new Searcher(userInputIndexDir);
        long startTime = System.currentTimeMillis();
        TopDocs hits = searcher.search(searchQuery);
        long endTime = System.currentTimeMillis();

        System.out.println(hits.totalHits +
                " documents found. Time :" + (endTime - startTime));
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            System.out.println("File: "
                    + doc.get(userInputDataDir));
        }
        searcher.close();
    }
}
