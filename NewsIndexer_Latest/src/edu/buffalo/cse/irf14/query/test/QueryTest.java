package edu.buffalo.cse.irf14.query.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;

public class QueryTest {

	@Test
	public void query_SingleTerm() {
		String queryString = "Term:Sample1";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{ " + queryString + " }", query.toString());
	}

	@Test
	public void query_SingleNotTerm() {
		String queryString = "NOT Place:Sample1";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{ <Place:Sample1> }", query.toString());
	}

	@Test
	public void query_TwoTermsDefault() {
		String queryString = "Term:Sample1 Author:Sample2";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{ Term:Sample1 OR Author:Sample2 }", query.toString());
	}

	@Test
	public void query_TwoTerms() {
		String queryString = "Place:Sample1 AND Category:Sample2";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{ Place:Sample1 AND Category:Sample2 }", query.toString());
	}

	@Test
	public void normalize_SingleTerm() {
		String queryString = "Sample1";
		assertEquals("Term:Sample1",
				QueryParser.normalizeQuery(queryString, ""));
	}

	@Test
	public void normalize_SingleTermWithIndex() {
		String queryString = "Place:Sample1";
		assertEquals("Place:Sample1",
				QueryParser.normalizeQuery(queryString, ""));
	}

	@Test
	public void normalize_SingleNotTerm() {
		String queryString = "NOT Sample1";
		assertEquals("NOT Term:Sample1",
				QueryParser.normalizeQuery(queryString, ""));
	}

	@Test
	public void normalize_TwoTerms() {
		String queryString = "Sample1 AND Sample2";
		assertEquals("Term:Sample1 AND Term:Sample2",
				QueryParser.normalizeQuery(queryString, ""));
	}

	@Test
	public void normalize_CompoundQuery() {
		String queryString = "Sample1 AND (Sample2 OR Place:Sample3)";
		assertEquals("Term:Sample1 AND (Term:Sample2 OR Place:Sample3)",
				QueryParser.normalizeQuery(queryString, ""));
	}
	
	@Test
	public void normalize_Quotes() {
		String queryString = "\"hello world\" AND hi";
		assertEquals("Term:\"hello world\" AND Term:hi", 
				QueryParser.normalizeQuery(queryString, ""));
	}

	@Test
	public void normalize_CommonIndexCompoundQuery() {
		String queryString = "Sample1 AND Place:(Sample2 OR Sample3)";
		assertEquals("Term:Sample1 AND (Place:Sample2 OR Place:Sample3)", 
				QueryParser.normalizeQuery(queryString, ""));
	}
	
	@Test
	public void normalize_CommonIndexCompoundQuery2() {
		String queryString = "Category:(Sample1 AND Sample4) AND Place:(Sample2 OR Sample3)";
		assertEquals("(Category:Sample1 AND Category:Sample4) AND (Place:Sample2 OR Place:Sample3)", 
				QueryParser.normalizeQuery(queryString, ""));
	}
	
	@Test
	public void test_1() {
		String queryString = "hello";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{ Term:hello }", query.toString());
	}
	
	@Test
	public void test_2() {
		String queryString = "hello world";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{ Term:hello OR Term:world }", query.toString());
	}
	
	@Test
	public void test_3() {
		String queryString = "\"hello world\"";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{ Term:\"hello world\" }", query.toString());
	}
	
	@Test
	public void test_4() {
		String queryString = "orange AND yellow";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{ Term:orange AND Term:yellow }", query.toString());
	}
	
	@Test
	public void test_5() {
		String queryString = "(black OR blue) AND bruises";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{ [ Term:black OR Term:blue ] AND Term:bruises }", query.toString());
	}
	
	@Test
	public void test_6() {
		String queryString = "Author:rushdie NOT jihad";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{ Author:rushdie AND <Term:jihad> }", query.toString());
	}
	
	@Test
	public void test_7() {
		String queryString = "Category:War AND Author:Dutt AND Place:Baghdad AND prisoners detainees rebels";
		Query query = QueryParser.parse(queryString, "OR");
		assertEquals("{ Category:War AND Author:Dutt AND Place:Baghdad AND [ Term:prisoners OR Term:detainees OR Term:rebels ] }", query.toString());
	}
	
	@Test
	public void test_8() {
		String queryString = "(Love NOT War) AND Category:(movies NOT crime)";
		Query query = QueryParser.parse(queryString, "AND");
		assertEquals("{ [ Term:Love AND <Term:War> ] AND [ Category:movies AND <Category:crime> ] }", query.toString());
	}
	
	@Test
	public void test_9() {
		String queryString = "Category:oil AND place:Dubai AND ( price OR cost )";
		String query = QueryParser.normalizeQuery(queryString, "");
		assertEquals("Category:oil AND place:Dubai AND (Term:price OR Term:cost)", query);
	}

}
