/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		// TODO YOU MUST IMPLEMENT THIS
		Document document = new Document();
		try
		{
		
		if (filename == null)
			throw new ParserException();
		if (filename.equals(""))
			throw new ParserException();
		File article = new File(filename);
		if (!article.exists())
			throw new ParserException();
		BufferedReader articleReader = null;
		try {
			articleReader = new BufferedReader(new FileReader(article));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = new String();
		String fileid = new String();
		String category = new String();
		String title = new String();
		String author = null;// new String();
		String authorOrg = null;// new String();
		String place = new String();
		String date = new String();
		StringBuilder contents = new StringBuilder();
		boolean skip = false;

		fileid = article.getName();
		String parentTemp = article.getParent();
		category = parentTemp.substring(parentTemp.lastIndexOf(92) + 1,
				parentTemp.length());
		try {
			while ((line = articleReader.readLine()) != null) // To get Title
			{
				if (!line.equals(""))
					break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		title = line;

		try {
			while ((line = articleReader.readLine()) != null) // To get place
				// and date
			{
				if (!line.equals(""))
					break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String splitString = new String();
		boolean isTitle = true;
		boolean isAlpha = false;
		// to check if it is still title
		for (int i = 0; i < line.length(); i++) {
			if (Character.isAlphabetic(line.charAt(i))) {
				isAlpha = true;
				if (!Character.isUpperCase(line.charAt(i)))
					isTitle = false;
			}
		}
		if (isTitle && isAlpha) { // then add the line to the title
			title = title + "\n" + line;
			try {
				while ((line = articleReader.readLine()) != null) // To get
					// place and
					// date
				{
					if (!line.equals(""))
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (line.contains("<AUTHOR>") && line.contains("</AUTHOR>")) {
			if (line.contains("By"))
				splitString = "By";
			else if (line.contains("by"))
				splitString = "by";
			else
				splitString = "BY";
			if (line.contains(",")) {
				String[] temp = line.split(",");
				String[] authorTemp = temp[0].split(splitString);
				author = authorTemp[1];
				temp[1] = temp[1].trim();
				authorOrg = temp[1].substring(0, temp[1].indexOf("<"));
			} else {
				String[] authorTemp = line.split(splitString);
				authorTemp[1] = authorTemp[1].trim();
				author = authorTemp[1].substring(0, authorTemp[1].indexOf("<"));
			}

			try {
				while ((line = articleReader.readLine()) != null) // To get
					// place and
					// date
				{
					if (!line.equals(""))
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		else if (line.contains("blah"))
			skip = true;
		if (!skip) {
			String pattern = "(?:jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may?|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|(nov|dec)(?:ember)?)";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(line.toLowerCase());
			if (m.find() && m.start() < (line.length()) / 2) {
				int index = line.lastIndexOf("-");
				if(index!=-1)
				{
					String[] temp = new String[2];
					temp[0] = line.substring(0, index);
					temp[1] = line.substring(index + 1, line.length());
					if(!temp[0].contains(",")){				//either place OR date. Since we already matched month, ther is no place.
						date=temp[0];
					}
					else
					{
						String[] locationDateTemp = temp[0].split(",");
						if (locationDateTemp.length == 2) {
							place = locationDateTemp[0];
							date = locationDateTemp[1];
						} else {
						place = locationDateTemp[0] + ", " + locationDateTemp[1];
						date = locationDateTemp[2];
						}
					}
					place = place.trim();
					date = date.trim();
					contents.append(temp[1].trim());
					contents.append(" ");
				}
				else if(line.indexOf(",")!=-1)
				{
					String []placeOrDateTemp=line.split(",");
					if(placeOrDateTemp[0].matches(pattern))
						date=placeOrDateTemp[0];
					else
						place=placeOrDateTemp[0];
					contents.append(placeOrDateTemp[1].trim());
					contents.append(" ");
					
				}
				
			} 
			else {
				contents.append(line);
				contents.append(" ");
			}

			try {
				while ((line = articleReader.readLine()) != null) {
					contents.append(line);
					contents.append(" ");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			contents.append("CRAP DOCUMENT");
		}
		/*
		 * }catch(Exception e) { throw new ParserException(); } finally {
		 * articleReader.close(); }
		 */


		/* System.out.println("**********DONE PROCESSING**********");
		 System.out.println("FILENAME : "+filename);
		 System.out.println("FILEID : "+fileid);
		 System.out.println("CATEGORY : "+category);
		 System.out.println("TITLE : "+title);
		 System.out.println("AUTHOR : "+author);
		 System.out.println("AUTHOR COMPANY : "+authorOrg);
		 System.out.println("PLACE : "+place.trim());
		 System.out.println("DATE : "+date);
		 System.out.println("Contents : \n"+contents);*/

		String content = contents.toString();

		document.setField(FieldNames.FILEID, fileid);
		document.setField(FieldNames.CATEGORY, category);
		document.setField(FieldNames.TITLE, title);
		document.setField(FieldNames.AUTHOR, author);
		document.setField(FieldNames.AUTHORORG, authorOrg);
		document.setField(FieldNames.PLACE, place);
		document.setField(FieldNames.NEWSDATE, date);
		document.setField(FieldNames.CONTENT, content);
		return document;
		
		}catch(Exception e)
		{
		e.printStackTrace();	
		}
		return document;
	}

}
