public class Randomizer{
    public static boolean DEBUG=false;

    public static void main(String[] args) {
        //if there more than zero command line arguments, use the the first one, else use the string specified
        if (args.length != 2){
            System.out.println("Usage: Randomizer input_file output_file");
            System.exit(2);
        }
        String inputFile=args[0];
        String outputFile=args[1];
        new ParseFile(inputFile,outputFile,DEBUG);
    }
}

