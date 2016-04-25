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

    public class JudgeAppointerPair{
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
                String [] tokens = line.trim().split("[:]");
                judgeAppointerMap.put(tokens[0],tokens[1]);
                if (FeatureVectorsCreator.DEBUG){
                    System.out.println("JudgeProcessor: judge: "+tokens[0]+" || appointer: "+tokens[1]);
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

  private String cleanJudgeName(String name){
     String cleanName=name.toLowerCase();
      cleanName=cleanName.substring(0,cleanName.indexOf(","));
      cleanName=cleanName.replace(".","");
      cleanName=cleanName.trim();
     return cleanName;
  }

    /**
     * Lookup Appointer for a given judge
     * @param judge
     * @return
     */
   public JudgeAppointerPair lookupAppointer(String judge){
       JudgeAppointerPair judgeAppointerPair=new JudgeAppointerPair();
       judgeAppointerPair.judgeFullName=null;
       judgeAppointerPair.appointerAndParty=null;

       for (Map.Entry<String,String>judgeEntry: judgeAppointerMap.entrySet()){
          if (judgeEntry.getKey().toLowerCase().contains(cleanJudgeName(judge))){
              judgeAppointerPair.judgeFullName=judgeEntry.getKey();
              judgeAppointerPair.appointerAndParty=judgeEntry.getValue();
          }
       }
       return judgeAppointerPair;
   }
}
