package edu.buffalo.cse.irf14.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Index {

	private IndexType type;
	Map<Integer, List<Integer>> map;

	public Index(IndexType type) {
		this.type = type;
		map = new HashMap<Integer, List<Integer>>();
	}

	public IndexType getType() {
		return type;
	}

	public void addRecord(int termId, int docId) {
		if(map.containsKey(termId)){
			map.get(termId).add(docId);
		}			
		else{
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(docId);
			map.put(termId, list);
		}			
	}

	public void write() {
		
	}
}