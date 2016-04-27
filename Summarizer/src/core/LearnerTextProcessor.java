package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;

public class LearnerTextProcessor {
    LearnerFiles learnerFiles;
    Integer lineNumber = 0; //for coordinating across files

    public LearnerTextProcessor(LearnerFiles learnerFiles, SummaryFiles summaryFiles) {
        this.learnerFiles = learnerFiles;
        processFile(summaryFiles);
        learnerFiles.closeFiles();
        summaryFiles.closeFiles();
    }


    private void processFile(SummaryFiles summaryFiles) {
        if (summaryFiles.oldSummaryBufferedReader == null) {
            return;
        }//just return if the buffered reader is null
        try {
            String oldLine = summaryFiles.readOldLine();
            while (oldLine != null) {
                oldLine = oldLine.trim();
                String classifiers = getValidClassifiersForActiveRow();
                if (!(classifiers == null)) {
                    summaryFiles.writeNewEntry(oldLine+"\t\t"+classifiers, true);
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
    }

    public String getValidClassifiersForActiveRow() {
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
