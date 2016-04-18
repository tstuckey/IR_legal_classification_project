import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class ParseFile {
    CorpusDefinition myScannedSrcData;
    Boolean DEBUG;
    Boolean STEM;

    //Match on  tab
    private final String TAB_PATTERN = "[\\t]";
    private Pattern mainSplitPattern = Pattern.compile(TAB_PATTERN);
    public DictionaryfromCorpusFileProcessing outputFile;

    private final String WHITESPACE_PATTERN = "[\\s]";
    private Pattern splitPattern = Pattern.compile(WHITESPACE_PATTERN);

    /**
     * ParseFile constructor actually reads and parses the file
     *
     * @param input_file  file to be read
     * @param output_file file to write out
     * @param debug       boolean value specifying whether or not to output debugging messages
     */
    public ParseFile(String input_file, String output_file, Boolean debug) {
        this.DEBUG = debug;

        myScannedSrcData=new CorpusDefinition();
        try {
            outputFile = new DictionaryfromCorpusFileProcessing(output_file);
            BufferedReader br = new BufferedReader(new FileReader(input_file));
            String fileRead = br.readLine();
            while (fileRead != null) {
                if ((DEBUG) && (myScannedSrcData.totalDocuments % 1000 == 0)) {
                    System.out.println("ParseFile: " + myScannedSrcData.totalDocuments
                            + " read line: " + fileRead);
                }
                //split on the mainSplitPattern defined in the class instance variables
                String[] token = mainSplitPattern.split(fileRead.trim());
                String timeStamp = null;//6 digits indicating hours minutes seconds
                String userID = null;
                Integer firstRank = 0;//0 indicates top 10 ranking; 30 indicates 30-39 ranking
                String query = null;

                Integer numTokens = token.length;

                if ((numTokens > 0) && (token[0] != null)) {
                    timeStamp = token[0];
                }
                if ((numTokens > 1) && (token[1] != null)) {
                    userID = token[1];
                }
                if ((numTokens > 2) && (token[2] != null)) {
                    firstRank = Integer.parseInt(token[2]);
                }
                if ((numTokens > 3) && (token[3] != null)) {
                    query = token[3];
                }
                if (query!=null){
                    //only write out contents if the query was non-null
                    outputFile.writeDocumentInfo(getDateTime(timeStamp).toString(),"\t",false);
                    outputFile.writeDocumentInfo(userID,"\t",false);
                    outputFile.writeDocumentInfo(firstRank.toString(),"\t",false);
                    outputFile.writeDocumentInfo(cleanPhrase(query),"",true);
                }
                myScannedSrcData.totalDocuments++;
                fileRead = br.readLine();
            }//end while
            br.close();
            outputFile.closeFile();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ParseFile had a problem:  " + e.getMessage());
            System.exit(3);
        }
    }

   private Long getDateTime(String timeStamp){
       DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
       Date date=null;
       Long unixDate=null;

       String hour = timeStamp.substring(0,2);
       String min = timeStamp.substring(2,4);
       String sec = timeStamp.substring(4,6);
       String mini_timeStamp= hour+":"+min+":"+sec;
       try {
            date = format.parse("20-12-1999 "+mini_timeStamp);
           unixDate =date.getTime();
       } catch (ParseException e) {
           e.printStackTrace();
       }
       //System.out.println("ParseFile: date is "+date);
       return unixDate;
   }
    /**
     * Clean up the query phrase
     * @param phrase
     * @return
     */
    private String cleanPhrase(String phrase){
        //remove punctuation
        String cleanTerm=additionalCleaning(phrase);
        return cleanTerm;
    }

    /**
     * Do some additional token cleaning
     *
     * @param raw_token
     * @return clean_token
     */
    private String additionalCleaning(String raw_token) {
        if (raw_token == null){
            return null;
        }
        String clean_token;
        clean_token = raw_token;
        //clean_token = clean_token.replace(".", "");
        //clean_token = clean_token.replace(",", "");
        clean_token = clean_token.replace("\"", "\\");
        //clean_token = clean_token.replace("\'", "\\");
        //clean_token = clean_token.replace("(", "");
        //clean_token = clean_token.replace(")", "");
        //clean_token = clean_token.replace("[", "");
        //clean_token = clean_token.replace("]", "");
        //clean_token = clean_token.replace("--", "");
        //clean_token = clean_token.replace(";", "");
        //clean_token = clean_token.replace(":", "");
        //clean_token = clean_token.replace("!", "");
        //clean_token = clean_token.replace("+", "");
        //clean_token = clean_token.replace("?", "");
        myScannedSrcData.totalWords++;
        return clean_token;
    }
}
