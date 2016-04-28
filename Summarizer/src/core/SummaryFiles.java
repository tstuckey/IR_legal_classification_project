package core;


import utilities.ClassificationType;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the reference to the output directory and the file handles thereof
 */
public class SummaryFiles {

    BufferedReader oldSummaryBufferedReader;
    BufferedWriter newSummaryBufferedWriter;
    String SUMMARY_FILE_DELIMTER="\t";

    public SummaryFiles(String oldSummaryFile,String newSummaryFile){
       //establish the Buffered Reader for the old summary file
       //establish the Buffered Writer for the new summary file

        oldSummaryBufferedReader = setupInputReader(oldSummaryFile);
        newSummaryBufferedWriter = setupOutputWriter(newSummaryFile);
    }

    /**
     * Setup a BufferedWriter for the given filename
     *
     * @param filename
     * @return reference to the new BufferedWriter
     */
    public BufferedWriter setupOutputWriter(String filename) {
        BufferedWriter mybufferedWriter = null;
        try {
            Files.deleteIfExists(FileSystems.getDefault().getPath(filename));
            File t_file = new File(filename);//create the handle
            t_file.createNewFile();//touch the file
            mybufferedWriter = new BufferedWriter(new FileWriter(t_file,true));
        } catch (FileNotFoundException e) {
            System.out.println("SummaryFiles: couln't find file "+filename);
        } catch (IOException e) {
            System.out.println("SummaryFiles: had a problem setting up file");
        }
        return mybufferedWriter;
    }

    /**
     * Create an instance of the BufferedRread to process the feature file
     *
     * @param readFile
     * @return reference to the BufferedReader
     */
    public BufferedReader setupInputReader(String readFile) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(readFile));
        } catch (FileNotFoundException e) {
            System.out.println("SummaryFiles: had a problem reading file");
            e.printStackTrace();
        }
        return br;
    }

    public String readOldLine(){
        String result=null;
        try{
            result=oldSummaryBufferedReader.readLine();
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Writeout the line of text and follow it up with a newline
      * @param value
     */
   public void writeNewEntry(String value, Boolean new_line){
       try {
           newSummaryBufferedWriter.write(value);
           if (new_line){
               newSummaryBufferedWriter.newLine();
           }else{
               newSummaryBufferedWriter.write(SUMMARY_FILE_DELIMTER);
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
   }


    public void closeFiles(){
        try {
            oldSummaryBufferedReader.close();
            newSummaryBufferedWriter.close();
            }
         catch (IOException e) {
            e.printStackTrace();
        }

    }

}