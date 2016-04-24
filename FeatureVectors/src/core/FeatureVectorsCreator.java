package core;

public class FeatureVectorsCreator {
    public static boolean DEBUG=true;
    public static String OPERATION;

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: FeatureVectorsCreator directory_with_src_texts feature-file.txt"  +
                    " output_directory train/dev/test ");
            System.exit(2);
        }
        String topDirectory= args[0];
        String featureFile= args[1];
        String outputDirectory= args[2];

        switch (args[3].toLowerCase()){
            case "train": OPERATION="train";
            case "dev": OPERATION="dev";
            case "test": OPERATION="test";
            default: OPERATION="train";
        }

        FeaturesProcessor featuresProcessor = new FeaturesProcessor(featureFile);
        OutputProcessor outputProcessor=new OutputProcessor(outputDirectory,featuresProcessor);
        DirectoryParser srcTextFiles=new DirectoryParser(topDirectory);
        new SourceTextProcessor(srcTextFiles.results,featuresProcessor,outputProcessor);
    }
}
