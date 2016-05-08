# Appendix E Summarizer Routine  
```java
package core;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Handles the Processing of the feature file and maintains the HashMap we'll reuse during
 * pcoesssing of source text
 */
public class LearnerFiles {
    HashMap<String,LineNumberReader> learerFileBufferedReaders;

    /**
     * Create LearnerFiles instance and populate the Hashmap with
     * key: filename of predictions for each classifier
     * value: LineNumberReader (variant of Buffered Reader)
     * @param file_list
     */
    public LearnerFiles(List file_list) {
        learerFileBufferedReaders= new HashMap<>();
        Iterator<File> myiterator = file_list.iterator();

        File t_file;
        //Iterate through the documents
        while (myiterator.hasNext()) {
            t_file = myiterator.next();
            try {
                LineNumberReader learnerLineReader = setupInputReader(t_file.getCanonicalPath());
                //populate the classifier with the classifier name and the buffered reader
                learerFileBufferedReaders.put(getClassFromFileName(t_file.getCanonicalPath()),learnerLineReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Extract the name of the classifier from the string name
     * @param filename whole filename being processed
     * @return the name of the classifer (filename with the LEARNER_PREFIX stripped off)
     */
    public String getClassFromFileName(String filename){
       String classifier=null;
       classifier=filename.substring(filename.indexOf(SummarizerProcessor.LEARNER_PREFIX));
       classifier=classifier.substring(SummarizerProcessor.LEARNER_PREFIX.length());
       if (SummarizerProcessor.DEBUG){
           System.out.println("LearnerFiles: classifier was: "+classifier);
       }
       return classifier;
   }

    /**
     * Create the LineNumberReader reference for each file
     * @param featureFile
     * @return
     */
    private LineNumberReader setupInputReader(String featureFile) {
        BufferedReader br = null;
        LineNumberReader lnr = null;
        try {
            br = new BufferedReader(new FileReader(featureFile));
            lnr = new LineNumberReader(br);
        } catch (FileNotFoundException e) {
            System.out.println("FeaturesProcessor: had a problem reading feature file");
            e.printStackTrace();
        }
        return lnr;
    }

    /**
     * Iterate through and close the reader files references
     */
    public void closeFiles(){
        for (Map.Entry<String, LineNumberReader>readerEntry : learerFileBufferedReaders.entrySet()){
            try {
                readerEntry.getValue().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
package core;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;

public class LearnerTextProcessor {
    LearnerFiles learnerFiles;
    Integer countOfDocsWithSomeClassification = 0;

    /**
     * Setup the class Instance, process the files, and close the files
     *
     * @param learnerFiles
     * @param summaryFiles
     */
    public LearnerTextProcessor(LearnerFiles learnerFiles, SummaryFiles summaryFiles) {
        this.learnerFiles = learnerFiles;
        processFile(summaryFiles);
        learnerFiles.closeFiles();
        summaryFiles.closeFiles();
    }


    /**
     * Iterate through each row of the old summary file, get the classifiers by looking at the same row across
     * each of the test prediction files, and write out the new value in the output summary file
     *
     * @param summaryFiles
     */
    private void processFile(SummaryFiles summaryFiles) {
        if (summaryFiles.oldSummaryBufferedReader == null) {
            return;
        }//just return if the buffered reader is null
        Integer lineNumber = 0; //for coordinating row across files
        try {
            String oldLine = summaryFiles.readOldLine();
            while (oldLine != null) {
                oldLine = oldLine.trim();
                String classifiers = getValidClassifiersForActiveRow(lineNumber);
                if (!(classifiers == null)) {
                    summaryFiles.writeNewEntry(oldLine + "\t\t" + classifiers, true);
                    countOfDocsWithSomeClassification = countOfDocsWithSomeClassification + 1;
                } else {
                    //no classifiers so just write out the oldLine
                    summaryFiles.writeNewEntry(oldLine, true);
                }
                lineNumber = lineNumber + 1;
                oldLine = summaryFiles.readOldLine();
            }//end line processing
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("processFile had a problem: " + e.getMessage());
            System.exit(3);
        }
        summaryFiles.writeNewEntry("\n\nWe had " + countOfDocsWithSomeClassification + " with at least one classification", true);
    }

    /**
     * Check each classifier prediction file, if the value for a given row for a given classifier prediction file is >1,
     * then document aligned to that row is in the classifier denoted by the classifiere prediction file name (and key
     * of the HashMap). Iterate across each classifier and concatenate the classifers together before returning the
     * concatenated string
     * @param lineNumber
     * @return
     */
    public String getValidClassifiersForActiveRow(Integer lineNumber) {
        String results = null;
        for (Map.Entry<String, LineNumberReader> learnerEntry : learnerFiles.learerFileBufferedReaders.entrySet()) {
            String classifier = learnerEntry.getKey();
            LineNumberReader lnr = learnerEntry.getValue();
            try {
                //increment to the current line number
                lnr.setLineNumber(lineNumber);
                String tmpValue = lnr.readLine();
                Double dblValue = new Double(tmpValue);
                //System.out.print(classifier+" : "+dblValue+" ");
                if (dblValue > 0) {
                    if (results != null) {
                        results = results + " : " + classifier;

                    } else {
                        results = classifier;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//end iterator
        return results;
    }

}
package core;

/**
 * Summarize the existing summary file for the test set documents against the results of each category
 * in the test predictions output directory
 */
public class SummarizerProcessor {
    public static boolean DEBUG = false;
    static String LEARNER_PREFIX = "predictions_test_"; //this is the file prefix we will use to search the specified
                                                        //directory for output

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: SummarizerProcessor directory_with_learneroutput old_summary_info_file new_summary_info_file");
            System.exit(2);
        }
        String learnerDirectory = args[0];
        String oldSummaryInfoFile= args[1];
        String newSummaryInfoFile= args[2];

        DirectoryParser directory = new DirectoryParser(learnerDirectory);
        //get the files with the LEARNER_PREFIX
        directory.getFilesbyPrefix(LEARNER_PREFIX);
        LearnerFiles learnerFiles = new LearnerFiles(directory.results);
        SummaryFiles summaryFiles= new SummaryFiles(oldSummaryInfoFile,newSummaryInfoFile);
        new LearnerTextProcessor(learnerFiles, summaryFiles);
    }
}
package core;


import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;

/**
 * Handles the reference to the output directory and the file handles thereof
 */
public class SummaryFiles {

    BufferedReader oldSummaryBufferedReader;
    BufferedWriter newSummaryBufferedWriter;
    String SUMMARY_FILE_DELIMTER="\t";

    /**
     * Setup the class variables to read the old summary file
     * and to write the new summary file
     * @param oldSummaryFile
     * @param newSummaryFile
     */
    public SummaryFiles(String oldSummaryFile,String newSummaryFile){
        oldSummaryBufferedReader = setupInputReader(oldSummaryFile);
        newSummaryBufferedWriter = setupOutputWriter(newSummaryFile);
    }

    /**
     * Setup a BufferedWriter for the given filename for the new summary file
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
     * Create an instance of the BufferedReader to process the old summary file
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

    /**
     * Reads a line of text from the existing summary file
     * @return
     */
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
     * Writeout the line of text to the new summary file and follow it up with a newline
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


    /**
     * Close the file handles
     */
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
```
