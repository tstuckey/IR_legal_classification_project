import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;

/**
 * Handles writing the dictionary out in plain text
 */
public class DictionaryfromCorpusFileProcessing {
    File myfile;
    BufferedWriter mybufferedWriter;

    public DictionaryfromCorpusFileProcessing(String dictionaryFileName) {
        try {
            Files.deleteIfExists(FileSystems.getDefault().getPath(dictionaryFileName));
            myfile = new File(dictionaryFileName);//create the handle
            myfile.createNewFile();//touch the file
            mybufferedWriter = new BufferedWriter(new FileWriter(myfile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeDocumentInfo(String term, String delimiter, Boolean endNewLine) {
        try {
            mybufferedWriter.write(term);
            mybufferedWriter.write(delimiter);
            mybufferedWriter.write(" ");//just space in between entries
            if (endNewLine) {
                mybufferedWriter.newLine();
            }
        } catch (IOException e) {
            System.out.println("Couldn't write out the term.");
            e.printStackTrace();
        }
    }

    /**
     * Try to close the file handle for the DictionaryfromDisk
     */
    public void closeFile() {
        if (myfile == null) return; //just return if the file handle is gone
        try {
            mybufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
