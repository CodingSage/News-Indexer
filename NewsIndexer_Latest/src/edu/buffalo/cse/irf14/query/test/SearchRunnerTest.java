package edu.buffalo.cse.irf14.query.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.PrintStream;

import org.junit.Test;

import edu.buffalo.cse.irf14.SearchRunner;
import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;

public class SearchRunnerTest {
	
	@Test
	public void testQueryPhrase() {
		String indexDir = "C:\\Users\\Vinayak\\Desktop\\CourseWork\\Information Retrieval\\Project 1\\Indexes";
		String corpusDir = "C:\\Users\\Vinayak\\Desktop\\CourseWork\\Information Retrieval\\Project 1\\news_collection";
		SearchRunner runner = new SearchRunner(indexDir, corpusDir, 'Q', System.out);
		runner.query("\"Net profit\"", ScoringModel.TFIDF);
	}

	@Test
	public void testQueryStringScoringModel() {
		String indexDir = "C:\\Users\\Vinayak\\Desktop\\CourseWork\\Information Retrieval\\Project 1\\Indexes";
		String corpusDir = "C:\\Users\\Vinayak\\Desktop\\CourseWork\\Information Retrieval\\Project 1\\news_collection";
		SearchRunner runner = new SearchRunner(indexDir, corpusDir, 'Q', System.out);
		runner.query("category:coffee beans", ScoringModel.TFIDF);
	}

	@Test
	public void testQueryFile() {
		String indexDir = "C:\\Users\\Vinayak\\Desktop\\CourseWork\\Information Retrieval\\Project 1\\Indexes";
		String corpusDir = "C:\\Users\\Vinayak\\Desktop\\CourseWork\\Information Retrieval\\Project 1\\news_collection";
		SearchRunner runner = new SearchRunner(indexDir, corpusDir, 'E', System.out);
		File file = new File("C:\\Users\\Vinayak\\Desktop\\CourseWork\\Information Retrieval\\Project 1\\test\\query.txt");
		runner.query(file);
	}

}
