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
