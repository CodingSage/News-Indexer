package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;
import edu.buffalo.cse.irf14.query.SearchEngine;
import edu.buffalo.cse.irf14.query.SearchResult;
import edu.buffalo.cse.irf14.query.SearchResultComparator;

/**
 * Main class to run the searcher. As before implement all TODO methods unless
 * marked for bonus
 * 
 * @author nikhillo
 *
 */
public class SearchRunner {

	private static final String DEFAULT_OPERATOR = "OR";
	private PrintStream stream;
	private String indexPath;
	private String corpusPath;
	private SearchEngine engine;

	public enum ScoringModel {
		TFIDF, OKAPI
	};

	/**
	 * Default (and only public) constuctor
	 * 
	 * @param indexDir
	 *            : The directory where the index resides
	 * @param corpusDir
	 *            : Directory where the (flattened) corpus resides
	 * @param mode
	 *            : Mode, one of Q or E
	 * @param stream
	 *            : Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir, char mode,
			PrintStream stream) {
		indexPath = indexDir;
		corpusPath = corpusDir;
		this.stream = stream;
		engine = new SearchEngine(indexDir, corpusDir);
	}

	/**
	 * Method to execute given query in the Q mode
	 * 
	 * @param userQuery
	 *            : Query to be parsed and executed
	 * @param model
	 *            : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		long startTime = System.currentTimeMillis();
		Query query = QueryParser.parse(userQuery, DEFAULT_OPERATOR);
		List<SearchResult> results = engine.search(query, model, false);
		long duration = startTime - System.currentTimeMillis();
		// write to stream
	}

	/**
	 * Method to execute queries in E mode
	 * 
	 * @param queryFile
	 *            : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(queryFile));
			String line = reader.readLine();
			int count = Integer.parseInt(line.split("=")[1]);
			List<String> res = new ArrayList<String>();
			while (count != 0) {
				count--;
				String query = reader.readLine();
				query.trim();
				if(query == "")
					continue;
				String id = query.split(":")[0];
				String q = query.split(":")[1];
				q = q.substring(1, q.length() - 1);

				Query a = QueryParser.parse(q, DEFAULT_OPERATOR);
				List<SearchResult> result = engine.search(a,
						ScoringModel.TFIDF, true);
				
				String i = id + ":{";
				int c = 0;
				for (SearchResult searchResult : result) {
					c++;
					if (c > 10)
						break;
					i += searchResult.getDocumentName() + "#"
							+ searchResult.getRelevancy()+", ";
				}
				i += "}";
				res.add(i);
			}
			// TODO check the file write
			String l = "numResults=" + res.size();
			for (String string : res) {
				l += "\n" + string;
			}
			stream.write(l.getBytes());
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * General cleanup method
	 */
	public void close() {
		// TODO : IMPLEMENT THIS METHOD
	}

	/**
	 * Method to indicate if wildcard queries are supported
	 * 
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		// TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return false;
	}

	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * 
	 * @return A Map containing the original query term as key and list of
	 *         possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		// TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
		return null;

	}

	/**
	 * Method to indicate if speel correct queries are supported
	 * 
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported() {
		// TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}

	/**
	 * Method to get ordered "full query" substitutions for a given misspelt
	 * query
	 * 
	 * @return : Ordered list of full corrections (null if none present) for the
	 *         given query
	 */
	public List<String> getCorrections() {
		// TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		return null;
	}
}
