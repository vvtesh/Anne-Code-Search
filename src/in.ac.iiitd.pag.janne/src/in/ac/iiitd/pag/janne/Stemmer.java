package in.ac.iiitd.pag.janne;

import java.util.HashSet;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.util.Version;

public class Stemmer {
	
	public static void main(String[] args) {
		String[] tokens = processQuery("array");
		for(String token: tokens) System.out.println(token);
	}
	
	public static String[] processQuery(String query) {
		String[] processedQuery = null;
		EnglishAnalyzer en_an = new EnglishAnalyzer(Version.LUCENE_34);
		QueryParser parser = new QueryParser(Version.LUCENE_34, "", en_an);
		try {			
			processedQuery = parser.parse(query).toString().split(" ");
		} catch (ParseException e) {
			e.printStackTrace();
			processedQuery = query.split(" ");
		}
		String[] querySplit = query.split(" ");
		
		HashSet<String> items = new HashSet<String>();
		for(int i=0; i<processedQuery.length; i++) {
			items.add(processedQuery[i]);
		}
		for(int i=0; i<querySplit.length; i++) {
			items.add(querySplit[i]);
		}
		String[] output = new String[items.size()];
		output = items.toArray(output);
		return output;
	}
}
