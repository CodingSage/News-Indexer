/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo Class responsible for writing indexes to disk
 */
public class IndexWriter {

	private String indexPath;

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *            : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		indexPath = indexDir;
	}

	/**
	 * Method to add the given Document to the index This method should take
	 * care of reading the filed values, passing them through corresponding
	 * analyzers and then indexing the results for each indexable field within
	 * the document.
	 * 
	 * @param d
	 *            : The Document to be added
	 * @throws IndexerException
	 *             : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {
		for (FieldNames field : FieldNames.values()) {
			String[] values = d.getField(field);
			String delimiter = delimiterMap(field);
			Tokenizer tokenizer = new Tokenizer(delimiter);
			for (String value : values) {
				try {
					TokenStream stream = tokenizer.consume(value);
					Analyzer analyzer = AnalyzerFactory.getInstance().getAnalyzerForField(field, stream);
					while (analyzer.increment()) {
					}
					TokenStream filteredStream = analyzer.getStream();
					filteredStream.reset();
					// index the stream
					
				} catch (TokenizerException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Method that indicates that all open resources must be closed and cleaned
	 * and that the entire indexing operation has been completed.
	 * 
	 * @throws IndexerException
	 *             : In case any error occurs
	 */
	public void close() throws IndexerException {
		// TODO
	}

	private Map<String, Integer> getIndex(FieldNames field) {
		return new HashMap<String, Integer>();
	}

	private String delimiterMap(FieldNames field) {
		if (field == FieldNames.AUTHOR)
			return "";
		return "";
	}
}
