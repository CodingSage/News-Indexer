/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo Class responsible for writing indexes to disk
 */
public class IndexWriter {

	private Map<String, Index> indexMap;
	private Map<String, Dictionary> dictionaryMap;
	private String indexPath;

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *            : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		indexPath = indexDir;
		indexMap = new HashMap<String, Index>();
		for (IndexType type : IndexType.values())
			indexMap.put(type.toString(), new Index(type));
		for (IndexType type : IndexType.values())
			dictionaryMap.put(type.toString(), new Dictionary(type));
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
		int docId = Integer.parseInt(d.getField(FieldNames.FILEID)[0]);
		for (FieldNames field : FieldNames.values()) {
			String[] values = d.getField(field);
			String delimiter = delimiterMap(field);
			Tokenizer tokenizer = new Tokenizer(delimiter);
			for (String value : values) {
				try {
					TokenStream stream = tokenizer.consume(value);
					Analyzer analyzer = AnalyzerFactory.getInstance()
							.getAnalyzerForField(field, stream);
					while (analyzer.increment()) {
					}
					TokenStream filteredStream = analyzer.getStream();
					filteredStream.reset();
					// index the stream

					IndexType type = getIndexType(field);
					while (filteredStream.hasNext()) {
						Token token = filteredStream.next();
						int termId = getDictionary(type)
								.getId(token.toString());
						getIndex(type).addRecord(termId, docId);
					}
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
		for (IndexType type : IndexType.values()) {
			getIndex(type).write();
			getDictionary(type).write();
		}
	}

	// TODO manage instances of dictionary and index
	private Dictionary getDictionary(IndexType type) {
		return dictionaryMap.get(type);
	}

	private Index getIndex(IndexType type) {
		return indexMap.get(type);
	}

	private IndexType getIndexType(FieldNames field) {
		if (field == FieldNames.AUTHOR || field == FieldNames.AUTHORORG)
			return IndexType.AUTHOR;
		if (field == FieldNames.CATEGORY)
			return IndexType.CATEGORY;
		if (field == FieldNames.PLACE)
			return IndexType.PLACE;
		else
			return IndexType.TERM;
	}

	// TODO delimter for each Field Name
	private String delimiterMap(FieldNames field) {
		return " ";
	}
}
