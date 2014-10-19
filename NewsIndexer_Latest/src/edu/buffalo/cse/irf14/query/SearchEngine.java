package edu.buffalo.cse.irf14.query;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
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
			FileInputStream fileIn = new FileInputStream(indexDir + "global");
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

	public List<SearchResult> search(Query query, ScoringModel model) {
		Map<String, List<DocumentInfo>> terms = getTermDocuments(query);
		List<DocumentInfo> d = evaluateQuery(query.getQuery(), terms);
		return null;
	}
	
	private List<DocumentInfo> evaluateQuery(ExpressionNode node, Map<String, List<DocumentInfo>> map){
		if(node.left.left == null && node.right.right == null){
			List<DocumentInfo> a = map.get(node.left.index + ":" + node.left.value);
			List<DocumentInfo> b = map.get(node.right.index + ":" + node.right.value);
			if(node.value.toLowerCase().equals("and") 
					&& !node.left.value.startsWith("~") && !node.right.value.startsWith("~"))
				return and(a, b);
			if(node.value.toLowerCase().equals("or") 
					&& !node.left.value.startsWith("~") && !node.right.value.startsWith("~"))
				return or(a, b);
			if(node.value.toLowerCase().equals("and") 
					&& !node.left.value.startsWith("~") && node.right.value.startsWith("~"))
				return and_not(a, b);
			if(node.value.toLowerCase().equals("and") 
					&& node.left.value.startsWith("~") && !node.right.value.startsWith("~"))
				return and_not(b, a);
			return and(a, b);
		}
		if(node.value.toLowerCase().equals("and"))
			return and(evaluateQuery(node.left, map), evaluateQuery(node.right, map));
		return or(evaluateQuery(node.left, map), evaluateQuery(node.right, map));
	}
	
	private List<DocumentInfo> and(List<DocumentInfo> a, List<DocumentInfo> b){
		List<DocumentInfo> small, big, res = new ArrayList<DocumentInfo>();
		if(a.size() > b.size()){
			small = b;
			big = a;
		}else{
			small = a;
			big = b;
		}
		for (DocumentInfo i : small) {
			if(big.contains(i))
				res.add(i);
		}
		return res;
	}
	
	private List<DocumentInfo> or(List<DocumentInfo> a, List<DocumentInfo> b){
		List<DocumentInfo> small, big;
		if(a.size() > b.size()){
			small = b;
			big = a;
		}else{
			small = a;
			big = b;
		}
		List<DocumentInfo> res = new ArrayList<DocumentInfo>(big);
		for (DocumentInfo i : small) {
			if(!big.contains(i))
				res.add(i);
		}
		return res;
	}

	private List<DocumentInfo> and_not(List<DocumentInfo> a, List<DocumentInfo> nb){
		List<DocumentInfo> res = new ArrayList<DocumentInfo>(a);
		for (DocumentInfo i : nb) {
			if(a.contains(i))
				res.remove(i);
		}
		return res;
	}

	private Map<String, List<DocumentInfo>> getTermDocuments(Query query) {
		Map<String, List<DocumentInfo>> result = new HashMap<String, List<DocumentInfo>>();
		for (IndexType indexType : IndexType.values()) {
			List<String> termList = new ArrayList<String>();
			extractLeaves(query.getQuery(), termList, indexType);
			LoadReader(indexPath, indexType.toString());
			for (String term : termList) {
				if (result.containsKey(indexType + ":" + term))
					continue;
				Integer id = dictionary.getId(term);
				List<DocumentInfo> postings = index.getPostings(id);
				result.put(indexType + ":" + term, postings);
			}
		}
		return result;
	}

	private void extractLeaves(ExpressionNode node, List<String> termList,
			IndexType type) {
		if (node.left == null && node.right == null 
				&& node.index.toLowerCase().equals(type.toString().toLowerCase()))
			termList.add(node.value);
		if (node.left != null)
			extractLeaves(node.left, termList, type);
		if (node.right != null)
			extractLeaves(node.right, termList, type);
	}

}
