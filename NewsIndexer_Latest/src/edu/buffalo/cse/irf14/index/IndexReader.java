/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nikhillo Class that emulates reading data back from a written index
 */
public class IndexReader {

	Dictionary dictionary;
	Index index;

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *            : The root directory from which the index is to be read. This
	 *            will be exactly the same directory as passed on IndexWriter.
	 *            In case you make subdirectories etc., you will have to handle
	 *            it accordingly.
	 * @param type
	 *            The {@link IndexType} to read from
	 */
	public IndexReader(String indexDir, IndexType type) {
		String dictionaryPath = indexDir + File.separator + "d_" + type;
		String indexPath = indexDir + File.separator + "i_" + type;
		LoadReader(dictionaryPath, indexPath);
	}

	private void LoadReader(String dictionaryPath, String indexPath) {
		try {
			FileInputStream fileIn = new FileInputStream(dictionaryPath);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			dictionary = (Dictionary) in.readObject();
			in.close();
			fileIn.close();

			fileIn = new FileInputStream(indexPath);
			in = new ObjectInputStream(fileIn);
			index = (Index) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Get total number of terms from the "key" dictionary associated with this
	 * index. A postings list is always created against the "key" dictionary
	 * 
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		return dictionary.getAllIds().size();
	}

	/**
	 * Get total number of terms from the "value" dictionary associated with
	 * this index. A postings list is always created with the "value" dictionary
	 * 
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		List<Integer> ids = index.getAllIds();
		List<String> docs = new ArrayList<String>();
		for (Integer id : ids) {
			List<DocumentInfo> docIds = index.getPostings(id);
			for (DocumentInfo info : docIds) {
				if (!docs.contains(info.getId()))
					docs.add(info.getId());
			}
		}
		return docs.size();
	}

	/**
	 * Method to get the postings for a given term. You can assume that the raw
	 * string that is used to query would be passed through the same Analyzer as
	 * the original field would have been.
	 * 
	 * @param term
	 *            : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the
	 *         number of occurrences as values if the given term was found, null
	 *         otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		Integer id = dictionary.getId(term);
		List<DocumentInfo> posting = index.getPostings(id);
		if (posting == null)
			return null;
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (DocumentInfo doc : posting)
			map.put(doc.getId(), doc.getCount());
		return map;
	}

	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * 
	 * @param k
	 *            : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values null
	 *         for invalid k values
	 */
	public List<String> getTopK(int k) {
		if (k < 1)
			return null;
		List<Integer> ids = index.getAllIds();
		List<DocumentInfo> info = new ArrayList<DocumentInfo>();
		for (Integer id : ids) {
			int sum = 0;
			List<DocumentInfo> doc = index.getPostings(id);
			for (DocumentInfo documentInfo : doc)
				sum += documentInfo.getCount();
			info.add(new DocumentInfo(id.toString(), sum));
		}
		Collections.sort(info);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < k; i++) {
			int id = Integer.parseInt(info.get(i).getId());
			list.add(dictionary.getKey(id));
		}
		return list;
	}

	/**
	 * Method to implement a simple boolean AND query on the given index
	 * 
	 * @param terms
	 *            The ordered set of terms to AND, similar to getPostings() the
	 *            terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key and
	 *         number of occurrences as the value, the number of occurrences
	 *         would be the sum of occurrences for each participating term.
	 *         return null if the given term list returns no results BONUS ONLY
	 */
	public Map<String, Integer> query(String... terms) {
		Map<String, Integer> res = null;
		List<List<DocumentInfo>> info = new ArrayList<List<DocumentInfo>>();
		Integer min = null, minIndex = null;
		ArrayList<String> aterms = new ArrayList<String>(Arrays.asList(terms));
		for (String term : aterms) {
			int id = dictionary.getId(term);
			List<DocumentInfo> i = index.getPostings(id);
			if (min == null || min > i.size()) {
				min = i.size();
				minIndex = aterms.indexOf(term);
			}
			info.add(new ArrayList<DocumentInfo>(i));
		}
		List<DocumentInfo> i = info.get(minIndex);
		for (DocumentInfo d : i) {
			int count = d.getCount();
			boolean flag = true;
			for (List<DocumentInfo> list : info) {
				if (!list.contains(d)) {
					flag = false;
					break;
				} else if (list != i) {
					int j = list.indexOf(new DocumentInfo(d.getId(), 0));
					count += list.get(j).getCount();
				}
			}
			if (flag) {
				if (res == null)
					res = new HashMap<String, Integer>();
				res.put(d.getId(), count);
			}
		}
		return res;
	}
}
