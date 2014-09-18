/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author nikhillo
 * Class that parses a given file into a Document
 */
public class Parser {
	/**
	 * Static method to parse the given file into the Document object
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException {
		// TODO YOU MUST IMPLEMENT THIS - parser
		Document document = new Document();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
			for (String line; (line = reader.readLine()) != null;) {
				String dateTime = "";
				if (!document.containsField(FieldNames.TITLE) && !line.isEmpty()){
					document.setField(FieldNames.TITLE, line);
					continue;
				}					
				if (!document.containsField(FieldNames.PLACE) && !line.isEmpty()){
					String metaline = line.split("-")[0];
					String[] lines = metaline.split(",");
					if(lines.length > 0){
						String place = "";
						for (int i = 0; i < lines.length; i++) {
							place += lines[i] + ",";
						}
						dateTime = lines[lines.length];
						document.setField(FieldNames.PLACE, place);
					}
					continue;
				}					
				if (!document.containsField(FieldNames.NEWSDATE) && !line.isEmpty()){
					document.setField(FieldNames.NEWSDATE, dateTime);
					continue;
				}
			}
			reader.close();
		} catch (Exception e) {
			throw new ParserException(e.getMessage());
		}
		return document;
	}

}
