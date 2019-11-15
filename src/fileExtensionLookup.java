import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class fileExtensionLookup {
    public static List<File> fileListToReturn;
    public static List<File> fileExtensionLookup(String directoryName) {
        File directoryFiles = new File(directoryName);
        fileListToReturn = new ArrayList<File>();
        // get all the files from a directory
        File[] fList = directoryFiles.listFiles();
        //fileListToReturn.addAll(Arrays.asList(fList));
        if (fList != null){
            for (File file : fList) {
                if (file.isFile())
                {
                    if(
                            file.getName().toLowerCase().endsWith(".html") ||
                                    file.getName().toLowerCase().endsWith(".htm") ||
                                    file.getName().toLowerCase().endsWith(".txt")
                    ){
                        fileListToReturn.add(file);
                    }
                } else if (file.isDirectory()) {
                    //fileListToReturn.addAll(fileExtensionLookup(file.getAbsolutePath()));
                    fileExtensionLookup(file.getAbsolutePath());
                }
            }
        }
        return fileListToReturn;
    }
}
