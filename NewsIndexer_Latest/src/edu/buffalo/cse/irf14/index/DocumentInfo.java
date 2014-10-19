package edu.buffalo.cse.irf14.index;

import java.io.Serializable;

public class DocumentInfo implements Comparable<DocumentInfo>, Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private int count;

	public DocumentInfo() {
	}

	public DocumentInfo(String id, int count) {
		this.id = id;
		this.count = count;
	}
	
	public DocumentInfo(String id, int count, String snippet) {
		this.id = id;
		this.count = count;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object info) {
		if (!(info instanceof DocumentInfo))
			return false;
		return id.equals(((DocumentInfo) info).getId());
	}

	@Override
	public int compareTo(DocumentInfo o) {
		if (count == o.getCount())
			return 0;
		return count < o.getCount() ? 1 : -1;
	}

	@Override
	public String toString() {
		return id + ":" + count;
	}
}
