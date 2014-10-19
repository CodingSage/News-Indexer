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
			FileInputStream fileIn = new FileInputStream(indexDir + File.separator + "global");
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

	public List<SearchResult> search(Query query, ScoringModel model, boolean fastSearch) {
		Map<String, List<DocumentInfo>> terms = getTermDocuments(query);
		List<SearchResult> relevanceResult = null;
		System.out.println("Postings for terms is :\n"+terms);
		List<DocumentInfo> d = evaluateQuery(query.getQuery(), terms);
		relevanceResult = calculateRelevanceOkapi(d,terms);
		Collections.sort(relevanceResult,new SearchResultComparator());
		//List<SearchResult> res = getSnippets(corpusPath, d);
		return relevanceResult;
	}
	
	private List<SearchResult> calculateRelevance(List<DocumentInfo> d,Map<String, List<DocumentInfo>> terms) {
		List<SearchResult> res = new ArrayList<SearchResult>();
		double []sumofSquares=new double[d.size()];
		double []documentScore=new double[d.size()];
		double []idf=new double[terms.size()];
		int countTerms=0;
		
		Iterator queryIterator = terms.entrySet().iterator();
		while(queryIterator.hasNext())
		{
			Map.Entry pairs=(Map.Entry)queryIterator.next();
			idf[countTerms++]=Math.log10(global.getTotalDocumentCount()/((ArrayList) pairs.getValue()).size());
		}
		int docCount=0;
		for(DocumentInfo info : d)	//List of docs in the final result of boolean expression
		{
			//DocId in the result
			Iterator it = terms.entrySet().iterator();
			float ans=0.0f;
	    	int sum=0;
		    while (it.hasNext()) {						//Iterating over all terms and their postings		    	
		        Map.Entry pairs = (Map.Entry)it.next();
		        ArrayList docs=(ArrayList)pairs.getValue();	//Postings of term
		        for(int i=0;i<docs.size();i++)
		        {
		        	DocumentInfo doc=(DocumentInfo)docs.get(i);
		        	if(info.getId().equals(doc.getId()))
		        		{
		        			sum=sum+(doc.getCount()*doc.getCount());		        			
		        		}
		        }		        
		    }
		    ans=(float) Math.sqrt(sum);
	        sumofSquares[docCount++]=ans;			
		}
		
		//To calculate final scores
		int count=0;
		for(DocumentInfo info : d)	//List of docs in the final result of boolean expression
		{
			//DocId in the result
			Iterator it = terms.entrySet().iterator();
			double tempAns=0;
	    	double sum=0;
	    	int tCount=0;
		    while (it.hasNext()) {						//Iterating over all terms and their postings		    	
		        Map.Entry pairs = (Map.Entry)it.next();
		        ArrayList docs=(ArrayList)pairs.getValue();	//Postings of term
		        for(int i=0;i<docs.size();i++)
		        {
		        	DocumentInfo doc=(DocumentInfo)docs.get(i);
		        	if(info.getId().equals(doc.getId()))
		        		{
		        			double temp=doc.getCount()/sumofSquares[count];
		        			tempAns+=temp*idf[tCount]*idf[tCount];
		        		}
		        }		        
		        tCount++;
		    }
	        documentScore[count]=tempAns/global.getDocumentLength(info.getId());
	        count++;
		}
		int counter=0;
		for (DocumentInfo info : d) {
			String id = info.getId();
			SearchResult search = new SearchResult();
			try {
				BufferedReader reader = new BufferedReader(new FileReader(corpusPath+File.separator+id));
				search.setTitle(reader.readLine());
				search.setDocumentName(id);
				search.setDocumentLength(global.getDocumentLength(id));
				//System.out.println(documentScore[counter]);
				search.setRelevancy(documentScore[counter]);
				counter++;
				res.add(search);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	private List<SearchResult> calculateRelevanceOkapi(List<DocumentInfo> d,Map<String, List<DocumentInfo>> terms)
	{
		List<SearchResult> res = new ArrayList<SearchResult>();
		double []sumofSquares=new double[d.size()];
		double []documentScore=new double[d.size()];
		double []idf=new double[terms.size()];
		int countTerms=0;
		//Calculating idf of terms in query
		Iterator queryIterator = terms.entrySet().iterator();
		while(queryIterator.hasNext())
		{
			Map.Entry pairs=(Map.Entry)queryIterator.next();
			idf[countTerms++]=Math.log10(global.getTotalDocumentCount()/((ArrayList) pairs.getValue()).size());
		}
		
		int docCount=0;
		System.out.println("OKAPI");
		for(DocumentInfo info : d)	//List of docs in the final result of boolean expression
		{
			//DocId in the result
			Iterator it = terms.entrySet().iterator();
			float ans=0.0f;
	    	double sum=0;
	    	int count=0;
	    	double k1=2,k3=1.5;
	    	double b=0.75;
	    	double average=global.getAverageDocumentLength();
		    while (it.hasNext()) {						//Iterating over all terms and their postings		    	
		        Map.Entry pairs = (Map.Entry)it.next();
		        ArrayList docs=(ArrayList)pairs.getValue();	//Postings of term
		        for(int i=0;i<docs.size();i++)
		        {
		        	DocumentInfo doc=(DocumentInfo)docs.get(i);
		        	if(info.getId().equals(doc.getId()))
		        		{
		        			double numerator=(k1+1)*doc.getCount() * (k3+1)*1;
		        			double denominator = ( k1*((1-b) + b * (global.getDocumentLength(doc.getId())/average))   + doc.getCount() ) * (k3+1) ;
		        			double okapi=idf[count]*numerator/denominator;
		        			sum+=okapi;
		        		}
		        }		
		        count++;
		    }
	        documentScore[docCount]=sum;		
	        String id = info.getId();
			SearchResult search = new SearchResult();
			try {
				BufferedReader reader = new BufferedReader(new FileReader(corpusPath+File.separator+id));
				search.setTitle(reader.readLine());
				search.setDocumentName(id);
				search.setDocumentLength(global.getDocumentLength(id));
				search.setRelevancy(documentScore[docCount]);
				//TODO: snippet generation, many terms to be searched in case of dynamic
				res.add(search);
			} catch (Exception e) {
				e.printStackTrace();
			}
			docCount++;
		}
		return res;
	}
	
	private List<SearchResult> getSnippets(String corpusPath, List<DocumentInfo> infos){
		List<SearchResult> res = new ArrayList<SearchResult>();
		for (DocumentInfo info : infos) {
			String id = info.getId();
			SearchResult search = new SearchResult();
			try {
				BufferedReader reader = new BufferedReader(new FileReader(corpusPath+File.separator+id));
				search.setTitle(reader.readLine());
				search.setDocumentName(id);
				search.setDocumentLength(global.getDocumentLength(id));
				
				//TODO: snippet generation, many terms to be searched in case of dynamic
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}				
		return res;
	}
	
	private List<DocumentInfo> evaluateQuery(ExpressionNode node, Map<String, List<DocumentInfo>> map){
		if(node == null)
			return new ArrayList<DocumentInfo>();
		if(node.left == null || node.right == null)
			return map.get(node.index.toUpperCase() + ":" + node.value);			
		if(node.left.left == null && node.right.right == null){
			List<DocumentInfo> a = map.get(node.left.index.toUpperCase() + ":" + node.left.value);
			List<DocumentInfo> b = map.get(node.right.index.toUpperCase() + ":" + node.right.value);
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
		if(a == null || b == null)
			return res;
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
		if(a == null)
			return b;
		if(b == null)
			return a;
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
		if(a == null)
			return new ArrayList<DocumentInfo>();
		if(nb == null)
			return a;
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
				String termStr = term; 
				if(term.startsWith("~"))
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
