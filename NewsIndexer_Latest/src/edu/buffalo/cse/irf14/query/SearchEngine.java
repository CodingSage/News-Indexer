package edu.buffalo.cse.irf14.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.index.Dictionary;
import edu.buffalo.cse.irf14.index.DocumentInfo;
import edu.buffalo.cse.irf14.index.Index;
import edu.buffalo.cse.irf14.index.IndexData;
import edu.buffalo.cse.irf14.index.IndexType;

public class SearchEngine {

	private String indexPath;
	private String corpusPath;
	private IndexData global;
	private Dictionary dictionary;
	private Index index;

	public SearchEngine(String indexDir, String corpusDir) {
		indexPath = indexDir;
		corpusPath = corpusDir;
		try {
			FileInputStream fileIn = new FileInputStream(indexDir
					+ File.separator + "global");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			global = (IndexData) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void LoadReader(String indexDir, String type) {
		String dictionaryPath = indexDir + File.separator + "d_" + type;
		String indexPath = indexDir + File.separator + "i_" + type;
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

	public List<SearchResult> search(Query query, ScoringModel model,
			boolean fastSearch) {
		Map<String, List<DocumentInfo>> terms = getTermDocuments(query);
		// System.out.println("Postings for terms is :\n" + terms);
		List<DocumentInfo> d = evaluateQuery(query.getQuery(), terms);
		List<SearchResult> relevanceResult;
		if (model == ScoringModel.OKAPI)
			relevanceResult = calculateRelevanceOkapi(d, terms);
		else
			relevanceResult = calculateRelevance(d, terms);
		Collections.sort(relevanceResult, new SearchResultComparator());
		if (!fastSearch)
			getSnippets(corpusPath, relevanceResult);
		return relevanceResult;
	}

	private List<SearchResult> calculateRelevance(List<DocumentInfo> d,
			Map<String, List<DocumentInfo>> terms) {
		List<SearchResult> res = new ArrayList<SearchResult>();
		double[] sumofSquares = new double[d.size()];
		double[] documentScore = new double[d.size()];
		double[] idf = new double[terms.size()];
		int countTerms = 0;

		Iterator queryIterator = terms.entrySet().iterator();
		while (queryIterator.hasNext()) { // To calculate idf of each term in
											// the query
			Map.Entry pairs = (Map.Entry) queryIterator.next();
			if (pairs.getValue() == null)
				idf[countTerms++] = 0;
			else {
				idf[countTerms++] = Math.log10(global.getTotalDocumentCount()
						/ ((ArrayList) pairs.getValue()).size());
			}
		}
		int docCount = 0;
		for (DocumentInfo info : d) // List of docs in the final result of
									// boolean expression
		{
			// DocId in the result
			Iterator it = terms.entrySet().iterator();
			float ans = 0.0f;
			int sum = 0;
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				ArrayList docs = (ArrayList) pairs.getValue();
				if (docs == null)
					continue;
				for (int i = 0; i < docs.size(); i++) {
					DocumentInfo doc = (DocumentInfo) docs.get(i);
					if (info.getId().equals(doc.getId()))
						sum = sum + (doc.getCount() * doc.getCount());
				}
			}
			ans = (float) Math.sqrt(sum);
			sumofSquares[docCount++] = ans;
		}

		// To calculate final scores
		int count = 0;
		for (DocumentInfo info : d) // List of docs in the final result of
									// boolean expression
		{
			// DocId in the result
			Iterator it = terms.entrySet().iterator();
			double tempAns = 0;
			double sum = 0;
			int tCount = 0;
			while (it.hasNext()) { // Iterating over all terms and their
									// postings
				Map.Entry pairs = (Map.Entry) it.next();
				ArrayList docs = (ArrayList) pairs.getValue(); // Postings of
																// term
				if (docs == null)
					continue;
				for (int i = 0; i < docs.size(); i++) {
					DocumentInfo doc = (DocumentInfo) docs.get(i);
					if (info.getId().equals(doc.getId())) {
						double temp = doc.getCount() / sumofSquares[count];
						tempAns += temp * idf[tCount]; // * idf[tCount]
					}
				}
				tCount++;
			}
			documentScore[count] = tempAns
					/ global.getDocumentLength(info.getId());
			count++;
		}
		if(documentScore.length == 0)
			return res;
		double max = documentScore[0];
		for (int z = 0; z < count; z++) {
			if (documentScore[z] > max)
				max = documentScore[z];
		}
		float a = 0.4f;
		for (int z = 0; z < count; z++)
			documentScore[z] = a + (1 - a) * (documentScore[z] / max);
		int counter = 0;
		for (DocumentInfo info : d) {
			String id = info.getId();
			SearchResult search = new SearchResult();
			search.setDocumentName(id);
			search.setDocumentLength(global.getDocumentLength(id));
			search.setRelevancy(documentScore[counter]);
			counter++;
			res.add(search);
		}
		return res;
	}

	private List<SearchResult> calculateRelevanceOkapi(List<DocumentInfo> d,
			Map<String, List<DocumentInfo>> terms) {
		List<SearchResult> res = new ArrayList<SearchResult>();
		double[] sumofSquares = new double[d.size()];
		double[] documentScore = new double[d.size()];
		double[] idf = new double[terms.size()];
		int countTerms = 0;
		// Calculating idf of terms in query
		Iterator queryIterator = terms.entrySet().iterator();
		while (queryIterator.hasNext()) {
			Map.Entry pairs = (Map.Entry) queryIterator.next();
			if (pairs.getValue() == null)
				idf[countTerms++] = 0;
			else
				idf[countTerms++] = Math.log10(global.getTotalDocumentCount()
						/ ((ArrayList) pairs.getValue()).size());
		}

		int docCount = 0;
		for (DocumentInfo info : d) // List of docs in the final result of
		// boolean expression
		{
			// DocId in the result
			Iterator it = terms.entrySet().iterator();
			float ans = 0.0f;
			double sum = 0;
			int count = 0;
			double k1 = 2, k3 = 1.5;
			double b = 0.75;
			double average = global.getAverageDocumentLength();
			while (it.hasNext()) { // Iterating over all terms and their
				// postings
				Map.Entry pairs = (Map.Entry) it.next();
				ArrayList docs = (ArrayList) pairs.getValue(); // Postings of
				// term
				for (int i = 0; i < docs.size(); i++) {
					DocumentInfo doc = (DocumentInfo) docs.get(i);
					if (info.getId().equals(doc.getId())) {
						double numerator = (k1 + 1) * doc.getCount() * (k3 + 1)
								* 1;
						double denominator = (k1
								* ((1 - b) + b
										* (global
												.getDocumentLength(doc.getId()) / average)) + doc
									.getCount())
								* (k3 + 1);
						double okapi = idf[count] * numerator / denominator;
						sum += okapi;
					}
				}
				count++;
			}
			documentScore[docCount] = sum;
			docCount++;
		}
		if(documentScore.length == 0)
			return res;
		double max = documentScore[0];
		for (int z = 0; z < docCount; z++) {
			if (documentScore[z] > max)
				max = documentScore[z];
		}
		float a = 0.4f;
		for (int z = 0; z < docCount; z++)
			documentScore[z] = a + (1 - a) * (documentScore[z] / max);
		int counter = 0;
		for (DocumentInfo info : d) {
			String id = info.getId();
			SearchResult search = new SearchResult();
			search.setDocumentName(id);
			search.setDocumentLength(global.getDocumentLength(id));
			search.setRelevancy(documentScore[counter]);
			counter++;
			res.add(search);
		}
		return res;
	}

	private void getSnippets(String corpusPath, List<SearchResult> infos) {
		int snippetSize = 5;
		for (SearchResult search : infos) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(
						corpusPath + File.separator + search.getDocumentName()));
				search.setTitle(reader.readLine());
				String snippet = "";
				for (int i = 0; i < snippetSize; i++)
					snippet += reader.readLine();
				snippet += "...";
				search.setSnippet(snippet);
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private List<DocumentInfo> evaluateQuery(ExpressionNode node,
			Map<String, List<DocumentInfo>> map) {
		if (node == null)
			return new ArrayList<DocumentInfo>();
		if (node.left == null || node.right == null) {
			List<DocumentInfo> list = map.get(node.index.toUpperCase() + ":"
					+ node.value);
			if (list == null)
				return new ArrayList<DocumentInfo>();
			return list;
		}
		if (node.left.left == null && node.right.right == null) {
			List<DocumentInfo> a = map.get(node.left.index.toUpperCase() + ":"
					+ node.left.value);
			List<DocumentInfo> b = map.get(node.right.index.toUpperCase() + ":"
					+ node.right.value);
			if (node.value.toLowerCase().equals("and")
					&& !node.left.value.startsWith("~")
					&& !node.right.value.startsWith("~"))
				return and(a, b);
			if (node.value.toLowerCase().equals("or")
					&& !node.left.value.startsWith("~")
					&& !node.right.value.startsWith("~"))
				return or(a, b);
			if (node.value.toLowerCase().equals("and")
					&& !node.left.value.startsWith("~")
					&& node.right.value.startsWith("~"))
				return and_not(a, b);
			if (node.value.toLowerCase().equals("and")
					&& node.left.value.startsWith("~")
					&& !node.right.value.startsWith("~"))
				return and_not(b, a);
			return and(a, b);
		}
		if (node.value.toLowerCase().equals("and"))
			return and(evaluateQuery(node.left, map),
					evaluateQuery(node.right, map));
		return or(evaluateQuery(node.left, map), evaluateQuery(node.right, map));
	}

	private List<DocumentInfo> and(List<DocumentInfo> a, List<DocumentInfo> b) {
		List<DocumentInfo> small, big, res = new ArrayList<DocumentInfo>();
		if (a == null || b == null)
			return res;
		if (a.size() > b.size()) {
			small = b;
			big = a;
		} else {
			small = a;
			big = b;
		}
		for (DocumentInfo i : small) {
			if (big.contains(i))
				res.add(i);
		}
		return res;
	}

	private List<DocumentInfo> or(List<DocumentInfo> a, List<DocumentInfo> b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		List<DocumentInfo> small, big;
		if (a.size() > b.size()) {
			small = b;
			big = a;
		} else {
			small = a;
			big = b;
		}
		List<DocumentInfo> res = new ArrayList<DocumentInfo>(big);
		for (DocumentInfo i : small) {
			if (!big.contains(i))
				res.add(i);
		}
		return res;
	}

	private List<DocumentInfo> and_not(List<DocumentInfo> a,
			List<DocumentInfo> nb) {
		if (a == null)
			return new ArrayList<DocumentInfo>();
		if (nb == null)
			return a;
		List<DocumentInfo> res = new ArrayList<DocumentInfo>(a);
		for (DocumentInfo i : nb) {
			if (a.contains(i))
				res.remove(i);
		}
		return res;
	}

	private Map<String, List<DocumentInfo>> getTermDocuments(Query query) {
		Map<String, List<DocumentInfo>> result = new HashMap<String, List<DocumentInfo>>();
		for (IndexType indexType : IndexType.values()) {
			List<String> termList = new ArrayList<String>();
			extractLeaves(query.getQuery(), termList, indexType);
			LoadReader(indexPath, indexType.toString().toUpperCase());
			for (String term : termList) {
				String termStr = filter(term, indexType);
				if (term.startsWith("~"))
					termStr = termStr.substring(1);
				if (result.containsKey(indexType + ":" + termStr))
					continue;
				Integer id = dictionary.getId(termStr);
				List<DocumentInfo> postings = index.getPostings(id);
				result.put(indexType + ":" + term, postings);
			}
		}
		return result;
	}

	private String filter(String term, IndexType indexType) {
		if(indexType == IndexType.PLACE)
			term = term.toUpperCase();
		TokenStream stream = new TokenStream(term);
		String res = "";
		Analyzer analyzer;
		AnalyzerFactory factory = AnalyzerFactory.getInstance();
		switch (indexType) {
		case AUTHOR:
			analyzer = factory.getAnalyzerForField(FieldNames.AUTHOR, stream);
			break;
		case CATEGORY:
			analyzer = factory.getAnalyzerForField(FieldNames.CATEGORY, stream);
			break;
		case PLACE:
			analyzer = factory.getAnalyzerForField(FieldNames.PLACE, stream);
			break;
		default:
			analyzer = factory.getAnalyzerForField(FieldNames.CONTENT, stream);
			break;
		}
		try {
			while (analyzer.increment()) {
			}
			stream = analyzer.getStream();
			stream.reset();
			while (stream.hasNext())
				res += stream.next().toString() + " ";
		} catch (TokenizerException e) {
			e.printStackTrace();
		}
		res = res.trim();
		return res;
	}

	private void extractLeaves(ExpressionNode node, List<String> termList,
			IndexType type) {
		if (node.left == null
				&& node.right == null
				&& node.index.toLowerCase().equals(
						type.toString().toLowerCase()))
			termList.add(node.value);
		if (node.left != null)
			extractLeaves(node.left, termList, type);
		if (node.right != null)
			extractLeaves(node.right, termList, type);
	}

}
