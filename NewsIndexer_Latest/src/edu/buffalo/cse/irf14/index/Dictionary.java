package edu.buffalo.cse.irf14.index;

import java.util.HashMap;
import java.util.Map;

public class Dictionary {

	private IndexType type;
	Map<String, Integer> map;
	int count;

	public Dictionary(IndexType type) {
		count = 0;
		this.type = type;
		map = new HashMap<String, Integer>();
	}

	public IndexType getType() {
		return type;
	}

	public int getId(String term) {
		if (map.containsKey(term))
			return map.get(term);
		else {
			map.put(term, ++count);
			return count;
		}
	}

	public void write() {

	}

}
