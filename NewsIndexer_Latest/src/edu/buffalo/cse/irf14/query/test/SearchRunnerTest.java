package edu.buffalo.cse.irf14.query.test;

import static org.junit.Assert.*;

import java.io.PrintStream;

import org.junit.Test;

import edu.buffalo.cse.irf14.SearchRunner;
import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;

public class SearchRunnerTest {

	@Test
	public void testQueryStringScoringModel() {
		String indexDir = "C:\\Users\\Vinayak\\Desktop\\CourseWork\\Information Retrieval\\Project 1\\Indexes";
		String corpusDir = "C:\\Users\\Vinayak\\Desktop\\CourseWork\\Information Retrieval\\Project 1\\news_collection";
		SearchRunner runner = new SearchRunner(indexDir, corpusDir, 'Q', null);
		runner.query("place:washington AND federal treasury", ScoringModel.TFIDF);
	}

	@Test
	public void testQueryFile() {
		fail("Not yet implemented");
	}

}
