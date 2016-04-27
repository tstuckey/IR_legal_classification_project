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
public class OutputProcessor {
    public final String SUMMARY_INFO_OUTPUT_PREFIX = "_Summary_info_";
    public final String FEATURE_FILE_DELIMTER = "\t";
    public final String SUMMARY_FILE_DELIMTER = "|";

    BufferedWriter summaryFileBufferedWriter;
    HashMap<String, BufferedWriter> featureFileBufferedWriters;

    public OutputProcessor(String output_directory, FeaturesProcessor featuresProcessor) {
        //setup the summary outputfile BufferedWriter
        summaryFileBufferedWriter = setupOutputWriter(output_directory + File.separator
                + SUMMARY_INFO_OUTPUT_PREFIX + FeatureVectorsCreator.OPERATION );
        featureFileBufferedWriters = new HashMap<>();
        //setup the BufferedWriter for each Classification Type
        for (Map.Entry<String, ClassificationType> entry : featuresProcessor.myClassifications.entrySet()) {
            String eachClassification = entry.getKey();
            featureFileBufferedWriters.put(eachClassification,
                    setupOutputWriter(output_directory + File.separator
                            + FeatureVectorsCreator.OPERATION + "_" + eachClassification.replace(" ","_")));
        }
    }

    /**
     * Setup a BufferedWriter for the given filename
     *
     * @param filename
     * @return reference to the new BufferedWriter
     */
    private BufferedWriter setupOutputWriter(String filename) {
        BufferedWriter mybufferedWriter = null;
        try {
            Files.deleteIfExists(FileSystems.getDefault().getPath(filename));
            File t_file = new File(filename);//create the handle
            t_file.createNewFile();//touch the file
            mybufferedWriter = new BufferedWriter(new FileWriter(t_file,true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mybufferedWriter;
    }

    public void iterateAndOutputFeatureVectors() {
/*        for (Map.Entry<String, ReverseCollectionInfo> entry : docToTerm.entrySet()) {
            String docID = entry.getKey();
            ReverseCollectionInfo termsInfo = entry.getValue();
            //Write the document information
            featureVectorFileProcessing.writeDocumentInfo(docID, termsInfo);
        }
        featureVectorFileProcessing.closeFile();*/
    }

    /**
     * Writeout the line of text and follow it up with a newline
      * @param value
     */
   public void writeSummaryEntry(String value, Boolean new_line){
       try {
           summaryFileBufferedWriter.write(value);
           if (new_line){
               summaryFileBufferedWriter.newLine();
           }else{
               summaryFileBufferedWriter.write(SUMMARY_FILE_DELIMTER);
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
   }


    /**
     * Find the BufferedWriter for the classifer
     * @param classifier
     * @return BufferedWriter referecne
     */
    public BufferedWriter getAppropriateBufferedWriter(String classifier){
       BufferedWriter correctBW=null;
        for (Map.Entry<String, BufferedWriter>bwEntry : featureFileBufferedWriters.entrySet()){
            //Be sure to test for full string equality between the key and the classifier
            //as some classifers are subsets of others
            //A contains test vs a equals test will have contents being written to unintended files
            if (bwEntry.getKey().equals(classifier)){
                correctBW = bwEntry.getValue();
            }
        }
        return correctBW;
    }

    /**
     * Write out a value and follow it up with either a carriage return (if new_line) is specified
     * or the delimiter for the feature file
     * @param classifierBufferedWriter
     * @param value
     * @param new_line
     */
    public void writeClassifierEntry(BufferedWriter classifierBufferedWriter, String value, Boolean new_line){

        if (classifierBufferedWriter==null){return;}//just head back if there isn't a BufferedWriter reference
        try {
            if (new_line){
                classifierBufferedWriter.write(value);
                classifierBufferedWriter.newLine();
            }else{
                classifierBufferedWriter.write(value);
                classifierBufferedWriter.write(FEATURE_FILE_DELIMTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeFiles(){
        try {
            summaryFileBufferedWriter.flush();
            summaryFileBufferedWriter.close();
            for (Map.Entry<String,BufferedWriter>bwEntry : featureFileBufferedWriters.entrySet()){
                bwEntry.getValue().flush();
                bwEntry.getValue().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
