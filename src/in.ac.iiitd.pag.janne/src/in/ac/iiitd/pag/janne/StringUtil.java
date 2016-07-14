package in.ac.iiitd.pag.janne;

public class StringUtil {
	public static String processRootText(String text) {
		String rootText = text;	
		if (text.length() > 20) {
			rootText = 	text.substring(0, 15)+" ... " +text.substring(text.length()-3,text.length());
		}
		return rootText;
	}
}
