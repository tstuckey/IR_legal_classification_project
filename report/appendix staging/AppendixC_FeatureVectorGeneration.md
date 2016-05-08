# Appendix C Feature Vector Generation
```java
package core;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import utilities.MydirectoryWalker;

import java.io.File;
import java.util.List;

/**
 * Stages a list of all the files with a give nsuffix in a given directory
 */
public class DirectoryParser {
    public File startDirectory;
    public List results;

    public DirectoryParser(String top_directory){
        this.startDirectory = null;
        this.results=null;
        try {
            startDirectory = new File(top_directory);
        } catch (Exception e) {
            System.err.println("Failed to find the directory " + top_directory);
            System.exit(1);
            e.printStackTrace();
        }
    }

    /**
     * Get files with a given suffix and store the internally in the resutls list
     * @param suffix
     */
    public void getFilesbySuffix(String suffix){
        MydirectoryWalker mywalker = new MydirectoryWalker(
                HiddenFileFilter.VISIBLE,
                FileFilterUtils.suffixFileFilter(suffix)
        );
        results = mywalker.parseDirectory(startDirectory);
    }

    /**
     * Get files with a given prefix and store them internally in the results list
     * @param prefix
     */
    public void getFilesbyPrefix(String prefix){
        MydirectoryWalker mywalker = new MydirectoryWalker(
                HiddenFileFilter.VISIBLE,
                FileFilterUtils.prefixFileFilter(prefix)
        );
        results = mywalker.parseDirectory(startDirectory);
    }


}package core;

import utilities.ClassificationType;
import utilities.DocumentInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the Processing of the feature file and maintains the HashMap we'll reuse during
 * pcoesssing of source text
 */
public class FeaturesProcessor {
    HashMap<String, ClassificationType> myClassifications; //key: classification name; value: classifcation info

    public FeaturesProcessor(String featureFile) {
        myClassifications = new HashMap<>();
        BufferedReader featureVectorBufferedReader = setupInputReader(featureFile);
        processFile(featureVectorBufferedReader);
    }

    /**
     * Create an instance of the BufferedRread to process the feature file
     *
     * @param featureFile
     * @return
     */
    private BufferedReader setupInputReader(String featureFile) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(featureFile));
        } catch (FileNotFoundException e) {
            System.out.println("FeaturesProcessor: had a problem reading feature file");
            e.printStackTrace();
        }
        return br;
    }

    /**
     * Process the Feature Vector File and populate the myClassifications HashMap structure
     */
    private void processFile(BufferedReader featureVectorBufferedReader) {
        if (featureVectorBufferedReader == null) {
            return;
        }//just return if the buffered reader is null
        try {
            String lastClassification = null;
            String line = featureVectorBufferedReader.readLine();
            while (line != null) {
                line=line.trim();
                if (line.endsWith(":")) {
                    //features end with a ':'
                    String aClassification = line.substring(0, line.length() - 1);
                    if (FeatureVectorsCreator.DEBUG) {
                        System.out.println("FeaturesProcessor: aClassification is: " + aClassification);
                    }
                    //shouldn't need to check as we are just processing one of the configuration files
                    //at this point, but just for consistency/safety, let's ensure we don't already
                    //have the key
                    if (!(myClassifications.containsKey(aClassification))) {
                        myClassifications.put(aClassification, new ClassificationType());
                        lastClassification = aClassification;
                    }
                }
                if (line.contains("|")) {
                    String[] tmp = line.split("[|]");
                    Integer featureID = Integer.parseInt(tmp[0]);
                    String feature = tmp[1];
                    if (FeatureVectorsCreator.DEBUG) {
                        System.out.println("FeaturesProcessor: adding to Classification: " + lastClassification +
                                " the feature: " + feature + " with featureID: " + featureID);
                    }
                    myClassifications.get(lastClassification).addFeature(feature,featureID);
                }
                line = featureVectorBufferedReader.readLine();
            }//end while
            featureVectorBufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FeaturesProcessor had a problem: " + e.getMessage());
            System.exit(3);
        }
    }

    /**
     * Clears out the Document feature counts so this instance can be reused for each
     * document processing routine
     */
    public void clearFeatureCounts() {
        for (Map.Entry<String, ClassificationType> classificationEntry : myClassifications.entrySet()) {
            classificationEntry.getValue().hasAnyFeatures = false;//reset the flag
            HashMap<String, DocumentInfo> t_featureHash = classificationEntry.getValue().featureHash;
            for (Map.Entry<String, DocumentInfo> featureEntry : t_featureHash.entrySet()) {
                featureEntry.getValue().featureCount = 0;//reset the invidual count for this feature
            }
        }
    }

}
package core;

public class FeatureVectorsCreator {
    public static boolean DEBUG = false;
    public static String OPERATION = "train";

    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Usage: FeatureVectorsCreator directory_with_src_texts feature-file.txt judge-appointer.txt" +
                    " output_directory train/dev/test ");
            System.exit(2);
        }
        String topDirectory = args[0];
        String featureFile = args[1];
        String judgeAppointerFile = args[2];
        String outputDirectory = args[3];

        if (args[4].toLowerCase().equals("train")){OPERATION="train";}
        if (args[4].toLowerCase().equals("dev")){OPERATION="dev";}
        if (args[4].toLowerCase().equals("test")){OPERATION="test";}

        FeaturesProcessor featuresProcessor = new FeaturesProcessor(featureFile);
        JudgeProcessor judgeProcessor = new JudgeProcessor(judgeAppointerFile);
        DirectoryParser directory = new DirectoryParser(topDirectory);
        directory.getFilesbySuffix(".txt");
        OutputProcessor outputProcessor = new OutputProcessor(outputDirectory, featuresProcessor);
        new SourceTextProcessor(directory.results, featuresProcessor, judgeProcessor, outputProcessor);
    }
}
package core;

import utilities.ClassificationType;
import utilities.DocumentInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the Processing of the Judge Appointer file into  a HashMap
 */
public class JudgeProcessor {
    HashMap<String, String> judgeAppointerMap; //key: Judge Name; value: Appointer name and political party

    public JudgeProcessor(String featureFile) {
        judgeAppointerMap = new HashMap<>();
        BufferedReader featureVectorBufferedReader = setupInputReader(featureFile);
        processFile(featureVectorBufferedReader);
    }

    public class JudgeAppointerPair {
        String judgeFullName;
        String appointerAndParty;
    }

    /**
     * Create an instance of the BufferedRead to process the feature file
     *
     * @param featureFile
     * @return
     */
    private BufferedReader setupInputReader(String featureFile) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(featureFile));
        } catch (FileNotFoundException e) {
            System.out.println("JudgeProcessor: had a problem reading feature file");
            e.printStackTrace();
        }
        return br;
    }

    /**
     * Process the Judge Appointer File and populate the HashMap structure
     */
    private void processFile(BufferedReader featureVectorBufferedReader) {
        if (featureVectorBufferedReader == null) {
            return;
        }//just return if the buffered reader is null
        try {
            String line = featureVectorBufferedReader.readLine();
            while (line != null) {
                String[] tokens = line.trim().split("[:]");
                judgeAppointerMap.put(tokens[0], tokens[1]);
                if (FeatureVectorsCreator.DEBUG) {
                    System.out.println("JudgeProcessor: judge: " + tokens[0] + " || appointer: " + tokens[1]);
                }
                line = featureVectorBufferedReader.readLine();
            }//end while
            featureVectorBufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("JudgeProcessor had a problem: " + e.getMessage());
            System.exit(4);
        }
    }

    private String cleanJudgeName(String name) {
        if ((name.equals("unknown")) ||
                (name.equals("chief justice")) ||
                (name.equals("per curiam"))  ||
                (!name.contains(","))){
            return name;
        }
        String cleanName = name.toLowerCase();
        cleanName = cleanName.substring(0, cleanName.indexOf(","));
        cleanName = cleanName.replace(".", "");
        cleanName = cleanName.trim();
        return cleanName;
    }

    /**
     * Lookup Appointer for a given judge
     *
     * @param judge
     * @return
     */
    public JudgeAppointerPair lookupAppointer(String judge) {
        JudgeAppointerPair judgeAppointerPair = new JudgeAppointerPair();
        judgeAppointerPair.judgeFullName = "unknown";
        judgeAppointerPair.appointerAndParty = "unknown";

        for (Map.Entry<String, String> judgeEntry : judgeAppointerMap.entrySet()) {
            if (judgeEntry.getKey().toLowerCase().contains(cleanJudgeName(judge))) {
                judgeAppointerPair.judgeFullName = judgeEntry.getKey();
                judgeAppointerPair.appointerAndParty = judgeEntry.getValue();
            }
        }
        return judgeAppointerPair;
    }
}
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
package core;

import utilities.ClassificationType;
import utilities.DocumentInfo;

import java.io.*;
import java.util.*;

public class SourceTextProcessor {
    List srcTextFiles;
    FeaturesProcessor featuresProcessor;
    OutputProcessor outputProcessor;
    Integer docCount;
    Integer overallWordCount;

    class CaseInfo {
        String caseName = "none";
        String opinionAuthor = "per curiam"; //default meaning general per the court, not Judge specific
        Integer docWordCount = 0;
    }

    public SourceTextProcessor(List files, FeaturesProcessor featuresProcessor, JudgeProcessor judgeProcessor,
                               OutputProcessor outputProcessor) {
        this.srcTextFiles = files;
        this.featuresProcessor = featuresProcessor;
        this.outputProcessor = outputProcessor;
        docCount = 0;
        overallWordCount = 0;

        Iterator<File> myiterator = srcTextFiles.iterator();
        File t_file;
        //Iterate through the documents
        while (myiterator.hasNext()) {
            t_file = myiterator.next();
            if (FeatureVectorsCreator.DEBUG) {
                System.out.println("SourceTextProcessor: Processing source text file: " + t_file);
            }
                try {
                    //clear out the counts in the featuresProcessor HashMap before we start processing the new file
                    featuresProcessor.clearFeatureCounts();
                    docCount++;
                    BufferedReader srcTextBufferedReader = setupInputReader(t_file.getCanonicalPath());
                    CaseInfo caseInfo = processFile(srcTextBufferedReader);
                    writeOutResults(caseInfo, featuresProcessor, judgeProcessor);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }//end document iteration
        System.out.println("Total Documents processed: " + docCount);
        System.out.println("Total Words processed: " + overallWordCount);
        outputProcessor.closeFiles();//close the file handles
    }

    /**
     * Create an instance of the BufferedRread to process the source text file
     *
     * @param srcFile
     * @return
     */
    private BufferedReader setupInputReader(String srcFile) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(srcFile));
        } catch (FileNotFoundException e) {
            System.out.println("SourceTextProcessor: had a problem reading  source textfile " + srcFile);
            e.printStackTrace();
        }
        return br;
    }

    private Integer countWordsinLine(String line) {
        String[] tokens = line.split("[\\s]");
        return tokens.length;
    }

    private CaseInfo processFile(BufferedReader srcTextBufferedReader) {
        if (srcTextBufferedReader == null) {
            return null;
        }//just return if the buffered reader is null
        CaseInfo caseInfo = new CaseInfo();
        try {
            Boolean checkCaseName = true;
            Boolean checkOpinionAuthor = true;
            Boolean inSyllabusSection = true;
            Boolean inOpinionSection = false;

            String line = srcTextBufferedReader.readLine();
            while (line != null) {
                line = line.trim();
                overallWordCount = overallWordCount + countWordsinLine(line); //keep track of the overall word count as a metric
                caseInfo.docWordCount = caseInfo.docWordCount + countWordsinLine(line);//keep track of the doc word count as a metric
                if ((inSyllabusSection) && (checkCaseName)) {
                    if ((line.toLowerCase().contains("v.")) &&
                            !(line.toLowerCase().contains("see"))
                            ) {
                        caseInfo.caseName = line;
                        if (FeatureVectorsCreator.DEBUG) {
                            System.out.println("\tSourceTextProcessor: Case Name is: " + caseInfo.caseName);
                        }
                        checkCaseName = false;
                    }
                }//end checkCaseName

                if ((inSyllabusSection) && (checkOpinionAuthor)) {
                    if (line.toLowerCase().contains("delivered the opinion")) {
                        if (line.toLowerCase().contains("chief justice")){
                            caseInfo.opinionAuthor = "chief justice";
                        }else{
                            if (line.toLowerCase().contains(".")){
                                caseInfo.opinionAuthor = line.substring(0, line.indexOf("."));
                            }else{
                                caseInfo.opinionAuthor = line.substring(0,line.indexOf(" delivered the opinion"));
                            }
                        }
                        if (FeatureVectorsCreator.DEBUG) {
                            System.out.println("\tSourceTextProcessor: Opinion Author is: " + caseInfo.opinionAuthor);
                        }
                        checkOpinionAuthor = false;
                    }
                }//end checkOpinionAuthor

                if (inSyllabusSection) {
                    if ((line.toLowerCase().startsWith("opinion of the court")) ||
                            (line.contains("per curiam"))) {
                        inSyllabusSection = false;
                        inOpinionSection = true;
                    }
                }//end syllabus processing
                if (inOpinionSection) {
                    //OK we're processing the main body of the opinion now
                    //Iterate through each classification
                    for (Map.Entry<String, ClassificationType> classificationEntry : featuresProcessor.myClassifications.entrySet()) {
                        HashMap<String, DocumentInfo> t_featureHash = classificationEntry.getValue().featureHash;
                        //System.out.println("\t\t\tSoureTextProcessor: looking at class: "+classificationEntry.getKey());
                        //Iterate through each each feature in each classification
                        for (Map.Entry<String, DocumentInfo> featureEntry : t_featureHash.entrySet()) {
                            String thisFeature = featureEntry.getKey();//get the feature text
                            //check to see if the line of source text contains thisFeature
                            if (line.toLowerCase().contains(thisFeature)) {
                                Integer currentFeatureCount = featureEntry.getValue().featureCount;
                                featureEntry.getValue().featureCount = currentFeatureCount+1;//increment the feature count
                                if (FeatureVectorsCreator.DEBUG) {
                                    System.out.println("\t\t\t\tSourceTextProcessor: got a match on class: " + classificationEntry.getKey() + " | for feature: " + thisFeature);
                                    System.out.println("\t\t\t\t\t Its featureCount is now: "+featureEntry.getValue().featureCount);
                                }
                                classificationEntry.getValue().hasAnyFeatures = true;//if any features for this classification
                                // are true, make sure this flag is set to true
                            }//end if the feature is contained
                        }//end feature iteration
                    }//end classification iteration
                } //end opinion processing
                line = srcTextBufferedReader.readLine();
            }//end line processing
            srcTextBufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("processFile had a problem: " + e.getMessage());
            System.exit(3);
        }
        return caseInfo;
    }

    /**
     * Write out the Results to the files; this starts off with the summary file and then iterates through each class
     * (one file per class) for all classes
     * @param caseInfo
     * @param featuresProcessor
     * @param judgeProcessor
     */
    private void writeOutResults(CaseInfo caseInfo, FeaturesProcessor featuresProcessor, JudgeProcessor judgeProcessor) {
        JudgeProcessor.JudgeAppointerPair ja = judgeProcessor.lookupAppointer(caseInfo.opinionAuthor);

        outputProcessor.writeSummaryEntry(caseInfo.caseName, false);
        outputProcessor.writeSummaryEntry(ja.judgeFullName, false);
        outputProcessor.writeSummaryEntry(ja.appointerAndParty, false);
        outputProcessor.writeSummaryEntry(caseInfo.docWordCount.toString(), true);

        //iterate through the classifiers and write out the info
        for (Map.Entry<String, ClassificationType> classificationTypeEntry : featuresProcessor.myClassifications.entrySet()) {
            //get the appropriate BufferWriter File Handle
            BufferedWriter bufferedWriter = outputProcessor.getAppropriateBufferedWriter(classificationTypeEntry.getKey());

            if (classificationTypeEntry.getValue().hasAnyFeatures) {
                if (FeatureVectorsCreator.DEBUG) {
                    System.out.println("YES, on doc: " + docCount + ", we had some features! for class: " + classificationTypeEntry.getKey());
                }
                //had some features
                Integer relevance = getRelevance(true, FeatureVectorsCreator.OPERATION);
                outputProcessor.writeClassifierEntry(bufferedWriter, relevance.toString(),false);
                iterateThroughFeatures(bufferedWriter, classificationTypeEntry.getValue().featureHash);
                outputProcessor.writeClassifierEntry(bufferedWriter,"",true);
            } else {
                if (FeatureVectorsCreator.DEBUG){
                    System.out.println("NO, on doc: "+docCount+", we had no features for class: "+classificationTypeEntry.getKey());
                }
                //had no features
                Integer relevance = getRelevance(false, FeatureVectorsCreator.OPERATION);
                outputProcessor.writeClassifierEntry(bufferedWriter, relevance.toString(),true);
            }
        }//end classification entry iteration
    }

    private void iterateThroughFeatures(BufferedWriter bufferedWriter, HashMap<String, DocumentInfo> featureHash){
        Map<String,DocumentInfo> sortedFeatureHash=getHashSortedbyCount(featureHash);
        for (Map.Entry<String,DocumentInfo> featureEntry : sortedFeatureHash.entrySet()){
            DocumentInfo documentInfo = featureEntry.getValue();
            if (documentInfo.featureCount>0) {
                outputProcessor.writeClassifierEntry(bufferedWriter, documentInfo.featureID + ":" + documentInfo.featureCount, false);
            }
        }
    }
    /**
     * Sort regular K,V Hashmap by V
     * @param unsortedMap
     * @return sortedMap
     */
    private Map<String, DocumentInfo> getHashSortedbyCount(Map<String, DocumentInfo> unsortedMap) {
        List<Map.Entry<String, DocumentInfo>> list = new LinkedList<Map.Entry<String, DocumentInfo>>(unsortedMap.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, DocumentInfo>>() {
            public int compare(Map.Entry<String, DocumentInfo> o1, Map.Entry<String, DocumentInfo> o2) {
                //sort in descending numeric value order
                return o1.getValue().featureID.compareTo(o2.getValue().featureID);
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, DocumentInfo> sortedMap = new LinkedHashMap<String, DocumentInfo>();
        for (Map.Entry<String, DocumentInfo> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /**
     * Determine the value of relevance to put return based on relevance and the mode of operation we are in.
     * Training and Dev mode, we write a 1 or a -1 based on the boolean parameter
     * Test mode, we always write a 0 as SVM will compute it for us
     *
     * @param hadFeatures
     * @param operation
     * @return
     */
    private Integer getRelevance(boolean hadFeatures, String operation) {
        Integer relevant = -1;
        if ((hadFeatures) && operation.equals("train")){
            relevant =1;
        }
        if ((hadFeatures) && operation.equals("dev")){
            relevant =1;
        }
        if (operation.equals("test")){
            relevant=0;
        }
        return relevant;
    }

}
package utilities;

import java.util.HashMap;

/**
 * Created by thomasstuckey on 4/23/16.
 */
public class ClassificationType {
    public HashMap<String, DocumentInfo> featureHash;//key: feature name;
                                                     // value: counts per Document for each feature
    public Boolean hasAnyFeatures = false;

    public ClassificationType() {
        this.featureHash = new HashMap<>();
    }

    public void addFeature(String feature, Integer featureID) {
            featureHash.put(feature,new DocumentInfo(featureID));
    }

}
package utilities;

/**
 * Created by thomasstuckey on 1/30/16.
 */
public class DocumentInfo {
    public Integer featureID = 0;
    public Integer featureCount = 0;

    public DocumentInfo(Integer t_featureID) {
        this.featureID=t_featureID;
    }

}
package utilities;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: thomasstuckey
 * Date: 7/23/12
 * Time: 9:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class MydirectoryWalker extends DirectoryWalker {

    public MydirectoryWalker(IOFileFilter dirFilter, IOFileFilter fileFilter) {
        super(dirFilter, fileFilter, -1);
    }

    /**Parse the directory by invoking the walk method that searches for file
     * names that match the
     *
     * @param startDirectory
     * @return
     */
    public List parseDirectory(File startDirectory){
        List results = new ArrayList();
        try {
            this.walk(startDirectory, results);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }


    /**Adds the file to the results.
     *
     * @param file to be added to the results
     * @param depth
     * @param results the list of files that meet the criteria.
     */
    protected void handleFile(File file, int depth, Collection results){
        results.add(file);
    }

}
```