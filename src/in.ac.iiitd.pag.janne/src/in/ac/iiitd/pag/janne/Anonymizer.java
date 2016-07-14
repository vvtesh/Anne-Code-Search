package in.ac.iiitd.pag.janne;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Take all files in src folder.
 * Move it to dest folder.
 * Rename the file to xyzuv.ext where xyzuv are some digits [0-9] and ext is the extension of the file.
 * @author Venkatesh
 *
 */
public class Anonymizer {
	public static void main(String[] args) {
		String src = "C:\\data\\svn\\iiitdsvn\\entity\\data\\assignments\\chetan-ap\\AP2014-Simple\\enums-only\\unanonymized";
		String dest = "C:\\data\\svn\\iiitdsvn\\entity\\data\\assignments\\chetan-ap\\AP2014-Simple\\enums-only\\anonymized";
		String PREFIX = "18";
		
		
		File[] srcFiles = new File(src).listFiles();
		int i = 0;
		for(File file: srcFiles) {
			String extn = file.getName().substring(file.getName().indexOf("."));
			if (FileUtil.isValidFileToOpenInEditor(file.getName())) {
				i++;
				String destFile = dest + "//" + PREFIX + intToString(i, 3) + extn;
				copy(file.getAbsolutePath(), destFile);
			}
		}
		
		
	}
	
   static String intToString(int num, int digits) {
	    StringBuffer s = new StringBuffer(digits);
	    int zeroes = digits - (int) (Math.log(num) / Math.log(10)) - 1; 
	    for (int i = 0; i < zeroes; i++) {
	        s.append(0);
	    }
	    return s.append(num).toString();
	}
	
	public static void copy(String srcFilePath, String dstFilePath) {
		try {
			File dstFile = new File(dstFilePath);
			File srcFile = new File(srcFilePath);
			Files.copy(srcFile.toPath(), dstFile.toPath());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
