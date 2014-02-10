package ca.yorku.cirillom.ensemble.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 8:45 PM.
 * <p/>
 * Description
 * <p/>
 * <p/>
 * <p/>
 * Created with IntelliJ IDEA.
 */
public class InputFileFilter extends FileFilter {

    /**
     * Tab Separated Values file extension (*.tsv)
     */
    public static final String TSV = "tsv";

    public boolean accept(File f) {

        // Accept all directories
        if (f.isDirectory()) {
            return true;
        }

        // Get file extension
        String extension = Util.getExtension(f);

        // Accept the following extensions
        return extension.equals(TSV);
    }

    //The description of this filter
    public String getDescription() {
        return "Tab Separated Values (*.tsv)";
    }
}
