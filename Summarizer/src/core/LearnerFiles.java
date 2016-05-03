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
     * Create the Hashmap of
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
     * Extract the name of the
     * @param filename
     * @return
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
     * Create an instance of the BufferedRread to process the feature file
     *
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
     * Iterate through and close the reader files
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
