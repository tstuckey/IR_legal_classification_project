package core;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import utilities.MydirectoryWalker;

import java.io.File;
import java.util.List;

/**
 * Stages a list of all the files with a .txt suffix in a given directory
 */
public class DirectoryParser {
    public List results; /*list of Files that match the criteria*/

    public DirectoryParser(String top_directory) {
        File startDirectory = null;
        try {
            startDirectory = new File(top_directory);
        } catch (Exception e) {
            System.err.println("Failed to find the directory " + top_directory);
            System.exit(1);
            e.printStackTrace();
        }

        //filter for files with the visibile files with the .txt suffix
        MydirectoryWalker mywalker = new MydirectoryWalker(
                HiddenFileFilter.VISIBLE,
                FileFilterUtils.suffixFileFilter(".txt")
        );
        results = mywalker.parseDirectory(startDirectory);
    }
}