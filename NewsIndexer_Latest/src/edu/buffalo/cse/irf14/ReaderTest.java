package edu.buffalo.cse.irf14;

import java.io.File;
import java.util.Map;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

public class ReaderTest {

	public static void main(String[] args) {
		String indexDir="F:\\UB Classes\\IR\\Index\\";
		String corpusDir="F:\\UB Classes\\IR\\Flat Index\\";
		char mode='E';
		SearchRunner sr=new SearchRunner(indexDir, corpusDir, mode, System.out);
		File queryFile=new File("F:\\UB Classes\\IR\\queryFile.txt");
		sr.query(queryFile);

	}

}

