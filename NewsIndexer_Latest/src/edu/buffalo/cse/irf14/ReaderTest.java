package edu.buffalo.cse.irf14;

import java.io.File;
import java.util.Map;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

public class ReaderTest {

	public static void main(String[] args) {
		String indexDir="C:\\Users\\Vinayak\\Desktop\\CourseWork\\Information Retrieval\\Project 1\\Indexes";
		String corpusDir="C:\\Users\\Vinayak\\Desktop\\CourseWork\\Information Retrieval\\Project 1\\news_collection";
		char mode='E';
		SearchRunner sr=new SearchRunner(indexDir, corpusDir, mode, System.out);
		File queryFile=new File("C:\\Users\\Vinayak\\Desktop\\CourseWork\\Information Retrieval\\Project 1\\test\\query.txt");
		sr.query(queryFile);

	}

}

