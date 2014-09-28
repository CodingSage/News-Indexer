package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dictionary implements Serializable {

	private static final long serialVersionUID = 1L;
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

	public int addTerm(String term) {
		if (term == null || term == "")
			return -1;
		map.put(term, ++count);
		return count;
	}

	public Integer getId(String term) {
		return map.get(term);
	}

	public List<String> getAllIds() {
		return new ArrayList<String>(map.keySet());
	}
}
