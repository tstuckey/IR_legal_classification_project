package core;

/**
 * Summarize the existing summary file for the test set documents against the results of each category
 * in the test predictions output directory
 */
public class SummarizerProcessor {
    public static boolean DEBUG = false;
    static String LEARNER_PREFIX = "predictions_test_"; //this is the file prefix we will use to search the specified
                                                        //directory for output

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: SummarizerProcessor directory_with_learneroutput old_summary_info_file new_summary_info_file");
            System.exit(2);
        }
        String learnerDirectory = args[0];
        String oldSummaryInfoFile= args[1];
        String newSummaryInfoFile= args[2];

        DirectoryParser directory = new DirectoryParser(learnerDirectory);
        //get the files with the LEARNER_PREFIX
        directory.getFilesbyPrefix(LEARNER_PREFIX);
        LearnerFiles learnerFiles = new LearnerFiles(directory.results);
        SummaryFiles summaryFiles= new SummaryFiles(oldSummaryInfoFile,newSummaryInfoFile);
        new LearnerTextProcessor(learnerFiles, summaryFiles);
    }
}
