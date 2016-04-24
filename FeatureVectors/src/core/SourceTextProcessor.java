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

    public SourceTextProcessor(List files, FeaturesProcessor featuresProcessor,
                               OutputProcessor outputProcessor) {
        this.srcTextFiles = files;
        this.featuresProcessor = featuresProcessor;
        this.outputProcessor = outputProcessor;

        Iterator<File> myiterator = srcTextFiles.iterator();
        File t_file;
        while (myiterator.hasNext()) {
            t_file = myiterator.next();
            if (FeatureVectorsCreator.DEBUG) {
                System.out.println("SourceTextProcessor: Processing source text file: " + t_file);
                try {
                    BufferedReader srcTextBufferedReader = setupInputReader(t_file.getCanonicalPath());
                    processFile(srcTextBufferedReader);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

    /**
     * Make the line of text lowercase and trim any whitespace off the ends
     *
     * @param line
     * @return normalized string
     */
    private String normalizeLine(String line) {
        if (line == null) {
            return null;
        }
        String tmp = line.toLowerCase();
        return tmp.trim();
    }

    private void processFile(BufferedReader srcTextBufferedReader) {
        //clear out the counts in the featuresProcessor HashMap before we start processing the new file
        featuresProcessor.clearFeatureCounts();
        if (srcTextBufferedReader == null) {
            return;
        }//just return if the buffered reader is null
        try {
            String caseName = "none";
            String opinionAuthor = "per curiam"; //default meaning general per the court, not Judge specific
            Boolean checkCaseName = true;
            Boolean checkOpinionAuthor = true;
            Boolean inSyllabusSection = true;
            Boolean inOpinionSection= false;

            String normalizedLine = normalizeLine(srcTextBufferedReader.readLine());
            while (normalizedLine != null) {
                if ((inSyllabusSection)&&(checkCaseName)) {
                    if ((normalizedLine.contains("v.")) &&
                            !(normalizedLine.contains("see"))
                            ) {
                        caseName = normalizedLine;
                        if (FeatureVectorsCreator.DEBUG) {
                            System.out.println("\tSourceTextProcessor: Case Name is: " + caseName);
                        }
                        checkCaseName = false;
                    }
                }//end checkCaseName

                if ((inSyllabusSection)&&(checkOpinionAuthor)) {
                    if (normalizedLine.contains("delivered the opinion")) {
                        opinionAuthor = normalizedLine.substring(0, normalizedLine.indexOf("."));
                        if (FeatureVectorsCreator.DEBUG) {
                            System.out.println("\tSourceTextProcessor: Opinion Author is: " + opinionAuthor);
                        }
                        checkOpinionAuthor = false;
                    }
                }//end checkOpinionAuthor

                if (inSyllabusSection) {
                    if ((normalizedLine.startsWith("opinion of the court")) ||
                            (normalizedLine.contains("per curiam"))) {
                        inSyllabusSection = false;
                        inOpinionSection = true;
                    }
                }//end syllabus processing
                if (inOpinionSection) {
                        //OK we're processing the main body of the opinion now
                        //Iterate through each classification
                        for (Map.Entry<String, ClassificationType> classificationEntry : featuresProcessor.myClassifications.entrySet()) {
                            HashMap<String, DocumentInfo> t_featureHash = classificationEntry.getValue().featureHash;
                            //Iterate through each each feature in the classification
                            for (Map.Entry<String, DocumentInfo> featureEntry : t_featureHash.entrySet()) {
                                String thisFeature = featureEntry.getKey();//get the feature text
                                //check to see if the line of source text cotains thisFeature
                                if (normalizedLine.contains(thisFeature)) {
                                    Integer currentFeatureCount = featureEntry.getValue().featureCount;
                                    featureEntry.getValue().featureCount = currentFeatureCount++;//increment the feature count
                                    if (FeatureVectorsCreator.DEBUG) {
                                        System.out.println("\t\t\t\tSourceTextProcessor: got a match on => " + thisFeature);
                                    }
                                    classificationEntry.getValue().hasAnyFeatures = true;//if any features for this classification
                                    // are true, make sure this flag is set to true
                                }
                            }
                        }
                } //end opinion processing
                normalizedLine = normalizeLine(srcTextBufferedReader.readLine());
            }//end while
            srcTextBufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("processFile had a problem: " + e.getMessage());
            System.exit(3);
        }
    }


}
