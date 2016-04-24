package core;

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

    /**
     * Process the Feature Vector File and populate the myClassifications HashMap structure
     */
    private void processFile(BufferedReader featureVectorBufferedReader) {
        if (featureVectorBufferedReader == null) {
            return;
        }//just return if the buffered reader is null
        try {
            String lastClassification = null;
            String normalizeLine = normalizeLine(featureVectorBufferedReader.readLine());
            while (normalizeLine != null) {
                if (normalizeLine.endsWith(":")) {
                    //features end with a ':'
                    String aClassification = normalizeLine.substring(0, normalizeLine.length() - 1);
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
                if (normalizeLine.contains("|")) {
                    String[] tmp = normalizeLine.split("[|]");
                    Integer featureID = Integer.parseInt(tmp[0]);
                    String feature = tmp[1];
                    if (FeatureVectorsCreator.DEBUG) {
                        System.out.println("FeaturesProcessor: adding to Classification: " + lastClassification +
                                " the feature: " + feature + " with featureID: " + featureID);
                    }
                    myClassifications.get(lastClassification).featureHash.put(feature, new DocumentInfo(featureID));
                }
                normalizeLine = normalizeLine(featureVectorBufferedReader.readLine());
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
