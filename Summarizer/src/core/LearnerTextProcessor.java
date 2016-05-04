package core;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;

public class LearnerTextProcessor {
    LearnerFiles learnerFiles;
    Integer countOfDocsWithSomeClassification = 0;

    /**
     * Setup the class Instance, process the files, and close the files
     *
     * @param learnerFiles
     * @param summaryFiles
     */
    public LearnerTextProcessor(LearnerFiles learnerFiles, SummaryFiles summaryFiles) {
        this.learnerFiles = learnerFiles;
        processFile(summaryFiles);
        learnerFiles.closeFiles();
        summaryFiles.closeFiles();
    }


    /**
     * Iterate through each row of the old summary file, get the classifiers by looking at the same row across
     * each of the test prediction files, and write out the new value in the output summary file
     *
     * @param summaryFiles
     */
    private void processFile(SummaryFiles summaryFiles) {
        if (summaryFiles.oldSummaryBufferedReader == null) {
            return;
        }//just return if the buffered reader is null
        Integer lineNumber = 0; //for coordinating row across files
        try {
            String oldLine = summaryFiles.readOldLine();
            while (oldLine != null) {
                oldLine = oldLine.trim();
                String classifiers = getValidClassifiersForActiveRow(lineNumber);
                if (!(classifiers == null)) {
                    summaryFiles.writeNewEntry(oldLine + "\t\t" + classifiers, true);
                    countOfDocsWithSomeClassification = countOfDocsWithSomeClassification + 1;
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
        summaryFiles.writeNewEntry("\n\nWe had " + countOfDocsWithSomeClassification + " with at least one classification", true);
    }

    /**
     * Check each classifier prediction file, if the value for a given row for a given classifier prediction file is >1,
     * then document aligned to that row is in the classifier denoted by the classifiere prediction file name (and key
     * of the HashMap). Iterate across each classifier and concatenate the classifers together before returning the
     * concatenated string
     * @param lineNumber
     * @return
     */
    public String getValidClassifiersForActiveRow(Integer lineNumber) {
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
