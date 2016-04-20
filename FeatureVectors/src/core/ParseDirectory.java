package core;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import utilities.MydirectoryWalker;

import java.io.File;
import java.util.List;

public class ParseDirectory {
    public List results; /*list of Files that match the criteria*/
    public ParseDirectory(String top_directory) {
        File startDirectory = null;
        try {
            startDirectory = new File(top_directory);
        } catch (Exception e) {
            System.err.println("Failed to find the directory " + top_directory);
            System.exit(1);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //System.out.println("have a file handle to " + startDirectory.toString());

        //filter for files with the visibile files with the .mm suffix
        MydirectoryWalker mywalker = new MydirectoryWalker(
                HiddenFileFilter.VISIBLE,
                FileFilterUtils.suffixFileFilter(".mm")
        );
        results = mywalker.parseDirectory(startDirectory);

    }


}