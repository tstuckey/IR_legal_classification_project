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
    public final String SUMMARY_INFO_OUTPUT = "_Summary info.tsv";
    public final String FEATURE_FILE_SUFFIX = ".tsv";

    BufferedWriter summaryFileBufferedWriter;
    HashMap<String, BufferedWriter> featureFileBufferedWriters;

    public OutputProcessor(String output_directory, FeaturesProcessor featuresProcessor) {
        //setup the summary outputfile BufferedWriter
        summaryFileBufferedWriter = setupOutputWriter(output_directory + File.separator + SUMMARY_INFO_OUTPUT);
        featureFileBufferedWriters = new HashMap<>();
        //setup the BufferedWriter for each Classification Type
        for (Map.Entry<String, ClassificationType> entry : featuresProcessor.myClassifications.entrySet()) {
            String eachClassification = entry.getKey();
            featureFileBufferedWriters.put(eachClassification,
                    setupOutputWriter(output_directory + File.separator
                            + FeatureVectorsCreator.OPERATION + "_" + eachClassification
                            + FEATURE_FILE_SUFFIX));
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
      * @param values
     */
   public void writeSummaryEntry(String values){
       try {
           summaryFileBufferedWriter.write(values);
           summaryFileBufferedWriter.newLine();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }


    /**
     * Find the BufferedWriter for the classifer
     * @param classifer
     * @return BufferedWriter referecne
     */
    private BufferedWriter getAppropriateBufferedWriter(String classifer){
       BufferedWriter correctBW=null;
        for (Map.Entry<String, BufferedWriter>bwEntry : featureFileBufferedWriters.entrySet()){
            if (bwEntry.getKey().equals(classifer)){
                correctBW = bwEntry.getValue();
            }
        }
        return correctBW;
    }

    /**
     * Writeout the line of text and follow it up with a newline
     * @param values
     */
    public void writeClassifierEntry(String classifier, String values){
        BufferedWriter bw = getAppropriateBufferedWriter(classifier);
        if (bw==null){return;}//just head back if there isn't a BufferedWriter reference
        try {
            bw.write(values);
            bw.newLine();
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
