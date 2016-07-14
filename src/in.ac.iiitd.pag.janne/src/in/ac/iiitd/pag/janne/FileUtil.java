package in.ac.iiitd.pag.janne;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Utility to apply methods on File.
 * @author Venkatesh
 *
 */
public class FileUtil {
	public static File[] listSourceFiles(File file) {
		File[] contents = file.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return (name.toLowerCase().endsWith(".java") || name.toLowerCase().endsWith(".c") || name.toLowerCase().endsWith(".cpp") );
		    }
		});
		return contents;
	}	
	public static boolean isValidFileToOpenInEditor(String name) {
		if (name.toLowerCase().endsWith(".java") || 
				name.toLowerCase().endsWith(".c") || 
				name.toLowerCase().endsWith(".cpp")
				) return true;
		return false;
	}
}
