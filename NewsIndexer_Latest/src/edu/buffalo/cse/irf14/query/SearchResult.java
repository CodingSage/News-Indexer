package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.buffalo.cse.irf14.index.DocumentInfo;

public class SearchResult {

	private String documentName;
	private int documentLength;
	private String title;
	private String snippet;
	private double relevancy;

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public int getDocumentLength() {
		return documentLength;
	}

	public void setDocumentLength(int documentLength) {
		this.documentLength = documentLength;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public double getRelevancy() {
		return relevancy;
	}

	public void setRelevancy(double relevancy) {
		this.relevancy = relevancy;
	}
}
