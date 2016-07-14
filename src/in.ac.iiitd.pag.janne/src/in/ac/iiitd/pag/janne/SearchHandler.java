package in.ac.iiitd.pag.janne;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Performs a line by line search for query terms.
 * QueryTerms are "AND"ed with.
 * Stemmed query terms are also used for search.
 * @author Venkatesh
 *
 */
public class SearchHandler {
	
	public static String[] searchANDonFullFile(String[] queryTerms, String filePath) {
		
		Hashtable<String, Integer> fileCounts = new Hashtable<String, Integer>();
		List<String> filteredFiles = new ArrayList<String>();
		
		File file = new File(filePath);
		File[] contents = FileUtil.listSourceFiles(file); 
		for(File content: contents) {
			for(String word: queryTerms) {											
				try {
					Grep.compile(word);	
					if (Grep.grep(content)) { 
						if (fileCounts.containsKey(content.getName())) {
							fileCounts.put(content.getName(), fileCounts.get(content.getName())+1); //found one more word.
						} else {
							fileCounts.put(content.getName(), 1);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		for (String fileName: fileCounts.keySet()) {
			int count = fileCounts.get(fileName);		
			if (count == queryTerms.length) { 	//doing an AND operation
				filteredFiles.add(fileName);
			}
		}
		String[] output = new String[filteredFiles.size()];
		output = filteredFiles.toArray(output);	
		return output;
	}
	
	public static String[] searchANDonLines(String[] queryTerms, String filePath) {
		
		List<String> filteredFiles = new ArrayList<String>();
		
		File file = new File(filePath);
		File[] contents = FileUtil.listSourceFiles(file); 
		for(File content: contents) {
			
		 
			String line = null;
			try {
				FileInputStream fis = new FileInputStream(content);
				 
				//Construct BufferedReader from InputStreamReader
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				
				while ((line = br.readLine()) != null) { //for each line in the file 
					int count = 0;
					for(String word: queryTerms) {	
						if (line.toLowerCase().contains(word)) {
							count++;
						}
					}
					if (count == queryTerms.length) {
						filteredFiles.add(content.getName());
						break;
					}
				}
			 
				br.close();
			} catch (Exception e) { };
			
		}
		String[] output = new String[filteredFiles.size()];
		output = filteredFiles.toArray(output);	
		return output;
	}
}
