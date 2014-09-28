package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Index implements Serializable {

	private static final long serialVersionUID = 1L;
	private IndexType type;
	Map<Integer, Set<DocumentInfo>> map;

	public Index(IndexType type) {
		this.type = type;
		map = new HashMap<Integer, Set<DocumentInfo>>();
	}

	public IndexType getType() {
		return type;
	}

	public void addRecord(int termId, String docId, int termFreq) {
		if (termId == -1)
			return;
		if (map.containsKey(termId)) {
			map.get(termId).add(new DocumentInfo(docId, termFreq));
		} else {
			HashSet<DocumentInfo> list = new HashSet<DocumentInfo>();
			list.add(new DocumentInfo(docId, termFreq));
			map.put(termId, list);
		}
	}

	public List<DocumentInfo> getPostings(Integer termId) {
		if (termId == null)
			return null;
		return new ArrayList<DocumentInfo>(map.get(termId));
	}

	public List<Integer> getAllIds() {
		return new ArrayList<Integer>(map.keySet());
	}
}