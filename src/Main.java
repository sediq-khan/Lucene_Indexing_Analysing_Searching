import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    private void search(String searchQuery) throws IOException {
        String htmlTitle = null;
        searcher = new Searcher(userInputIndexDir);
        TopDocs hits = null;
        long startTime = System.currentTimeMillis();
        try {

            hits = searcher.search(searchQuery);

        } catch (ParseException ex) {
            System.out.println("Parse Exception: " + ex.getMessage());
        }
        long endTime = System.currentTimeMillis();

        System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
        int i = 0;
        System.out.println("\n ############## << SEARCH INFO >> ########################");
        String title = "";
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            i++;
            Document doc = searcher.getDocument(scoreDoc);
            File tempFile = new File(doc.get("FILE_PATH"));
            if (tempFile.getName().endsWith(".html") || tempFile.getName().endsWith(".htm")){
                org.jsoup.nodes.Document htmlDoc = Jsoup.parse(tempFile, "UTF-8");
                title = htmlDoc.title();
            }
            long milliseconds = tempFile.lastModified();
            DateFormat format=new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
            long timeModified = tempFile.lastModified();
            System.out.println("\n");
            System.out.println("File: " + doc.get("FILE_NAME") + "\n" +
                    " Ranked: " + i + "\n" +
                    " File Path: " + doc.get("FILE_PATH") + "\n" +
                    " Score: " + scoreDoc.score + "\n" +
                    "Last Modified: " + format.format(milliseconds) +
                    "\n Title: " + title
                    + "\n"
            );
            title = "";
        }
        System.out.println("\n ############## << END SEARCH >> ########################");
        searcher.close();
    }
}
