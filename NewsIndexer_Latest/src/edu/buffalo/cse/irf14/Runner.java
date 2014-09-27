/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.File;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;

/**
 * @author nikhillo
 *
 */
public class Runner {
	/**
	 * 
	 */
	public Runner() {}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime=System.currentTimeMillis();
		System.out.println("Start");
		String ipDir = args[0];
		String indexDir = args[1];
		//more? idk!

		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();

		String[] files;
		File dir;

		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);

		try {
			for (String cat : catDirectories) {
				//if(cat.equals("zinc"))
				//{
					dir = new File(ipDir+ File.separator+ cat);
					files = dir.list();
					if (files == null)
						continue;

					for (String f : files) {
						//if(f.equals("0009584")){
						//System.out.println("===================Filename : "+f);
							try {
								d = Parser.parse(dir.getAbsolutePath() + File.separator +f);
								writer.addDocument(d);
							} catch (ParserException e) {						
								e.printStackTrace();
							} 
						//}
					}
				//}				//closing bracket for the 1st if ie folder name
			}
			writer.close();
			System.out.println("End");
			long endTime=System.currentTimeMillis();
			long totalTime=endTime-startTime;
			System.out.println("Total running time : "+totalTime);
		} catch (IndexerException e) {
			e.printStackTrace();
		}
	}

}
