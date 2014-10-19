package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class IndexData implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<String, Integer> docData;

	public IndexData() {
		docData = new HashMap<String, Integer>();
	}

	public void addDocument(String docName, int docLength) {
		if (!docData.containsKey(docName))
			docData.put(docName, docLength);
	}

	public int getDocumentLength(String docName) {
		if (!docData.containsKey(docName))
			return docData.get(docName);
		return -1;
	}

	public int getTotalDocumentCount() {
		return docData.size();
	}
}
