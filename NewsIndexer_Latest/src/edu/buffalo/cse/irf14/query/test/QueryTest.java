package edu.buffalo.cse.irf14.query.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;

public class QueryTest {

	@Test
	public void query_SingleTerm() {
		String queryString = "Sample1";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{" + queryString + "}", query.toString());
	}
	
	@Test
	public void query_SingleNotTerm() {
		String queryString = "NOT Sample1";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{<Sample1>}", query.toString());
	}

	@Test
	public void query_TwoTermsDefault() {
		String queryString = "Sample1 Sample2";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{Sample1 OR Sample2}", query.toString());
	}

	@Test
	public void query_TwoTerms() {
		String queryString = "Sample1 AND Sample2";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{" + queryString + "}", query.toString());
	}

	@Test
	public void query_CompoundQuery() {
		String queryString = "Sample1 AND (Sample2 OR Sample3)";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{Sample1 AND [Sample2 OR Sample3]}", query.toString());
	}

}
