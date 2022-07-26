package core;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import utilities.MydirectoryWalker;

import java.io.File;
import java.util.List;

/**
 * Stages a list of all the files with a give nsuffix in a given directory
 */
public class DirectoryParser {
    public File startDirectory;
    public List results;

    public DirectoryParser(String top_directory){
        this.startDirectory = null;
        this.results=null;
        try {
            startDirectory = new File(top_directory);
        } catch (Exception e) {
            System.err.println("Failed to find the directory " + top_directory);
            System.exit(1);
            e.printStackTrace();
        }
    }

    /**
     * Get files with a given suffix and store the internally in the resutls list
     * @param suffix
     */
    public void getFilesbySuffix(String suffix){
        MydirectoryWalker mywalker = new MydirectoryWalker(
                HiddenFileFilter.VISIBLE,
                FileFilterUtils.suffixFileFilter(suffix)
        );
        results = mywalker.parseDirectory(startDirectory);
    }

    /**
     * Get files with a given prefix and store them internally in the results list
     * @param prefix
     */
    public void getFilesbyPrefix(String prefix){
        MydirectoryWalker mywalker = new MydirectoryWalker(
                HiddenFileFilter.VISIBLE,
                FileFilterUtils.prefixFileFilter(prefix)
        );
        results = mywalker.parseDirectory(startDirectory);
    }


}