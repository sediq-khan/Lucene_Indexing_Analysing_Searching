import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class LuceneOperations {

    private IndexWriter writer;

    public LuceneOperations(String indexDirectoryPath) throws IOException {
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath));

        writer = new IndexWriter(indexDirectory,
                new StandardAnalyzer(Version.LUCENE_36), true,
                IndexWriter.MaxFieldLength.UNLIMITED);
    }

    public void close() throws CorruptIndexException, IOException {
        writer.close();
    }

    private Document getDocument(File file) throws IOException{
        Document document = new Document();

        Field contentField = new Field("CONTENTS", new FileReader(file));
        Field fileNameField = new Field("FILE_NAME", file.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED);
        Field filePathField = new Field("FILE_PATH", file.getCanonicalPath(), Field.Store.YES, Field.Index.NOT_ANALYZED);

        document.add(contentField);
        document.add(fileNameField);
        document.add(filePathField);
        return document;
    }

    private void indexFile(File file) throws IOException {
        File files = file;
        System.out.println("Indexing " + file.getCanonicalPath());
        Document document = getDocument(files);
        writer.addDocument(document);
    }

    public int createIndex(List<File> files) throws IOException{
        for (int i = 0; i < files.size(); i++){
            if (!files.get(i).isHidden()
                    && files.get(i).exists()
                    && files.get(i).canRead()
            ){
                indexFile(files.get(i));
            }
        }
        return writer.numDocs();
    }
}

