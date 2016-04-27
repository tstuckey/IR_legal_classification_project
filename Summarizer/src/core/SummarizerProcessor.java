package core;

public class SummarizerProcessor {
    public static boolean DEBUG = false;
    static String LEARNER_PREFIX = "predictions_test_";

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: SummarizerProcessor directory_with_learneroutput feature-file old_summary_info_file new_summary_info_file");
            System.exit(2);
        }
        String learnerDirectory = args[0];
        String featureFile = args[1];
        String oldSummaryInfoFile= args[2];
        String newSummaryInfoFile= args[3];

        DirectoryParser directory = new DirectoryParser(learnerDirectory);
        //get those files with the LEARNER_PREFIX
        directory.getFilesbyPrefix(LEARNER_PREFIX);
        LearnerFiles learnerFiles = new LearnerFiles(directory.results);
        SummaryFiles summaryFiles= new SummaryFiles(oldSummaryInfoFile,newSummaryInfoFile);
        new LearnerTextProcessor(learnerFiles, summaryFiles);
    }
}
