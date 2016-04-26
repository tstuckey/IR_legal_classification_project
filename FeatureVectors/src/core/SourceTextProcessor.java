package core;

import utilities.ClassificationType;
import utilities.DocumentInfo;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        for (Map.Entry<String,DocumentInfo> featureEntry : featureHash.entrySet()){
            String featureName = featureEntry.getKey();
            DocumentInfo documentInfo = featureEntry.getValue();
            if (documentInfo.featureCount>0) {
                outputProcessor.writeClassifierEntry(bufferedWriter, documentInfo.featureID + ":" + documentInfo.featureCount, false);
            }
        }
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
