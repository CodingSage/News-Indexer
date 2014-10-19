package edu.buffalo.cse.irf14.query;

import java.util.Comparator;

public class SearchResultComparator implements Comparator<SearchResult> {
	 @Override
	    public int compare(SearchResult sr1, SearchResult sr2) {
	        if(sr1.getRelevancy() < sr2.getRelevancy()){
	            return 1;
	        } else {
	            return -1;
	        }
	    }
}
