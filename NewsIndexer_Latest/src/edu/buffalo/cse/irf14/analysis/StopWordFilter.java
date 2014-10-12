
package edu.buffalo.cse.irf14.analysis;

import java.util.Arrays;
import java.util.List;

public class StopWordFilter extends TokenFilter{
	final static List<String> stopWordList=Arrays.asList("a","able","about","acrss","after","all","almost","am","among","an","and","any","are","as","at","be","because",
			"been","but","by","can","cannot","could","dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his",
			"how","however","i","if","in","into","is","it","its","just","least","let","like","likely","may","me","might","most","must","my","neither","no","not","nor","of","off",
			"often","on","only","or","other","our","own","rather","said","say","says","she","should","since","so","some","such","than","that","the","their","them","then","there","these",
			"they","this","tis","to","too","twas","us","want","wants","was","we","were","what","when","where","which","while","who","whom","why","will","with","would","yet","you","your");

	public StopWordFilter(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		return analyse(stream.next());	
	}

	public boolean evaluateCurrent() throws TokenizerException{
		return analyse(stream.getCurrent());	
	}

	private boolean analyse(Token token) throws TokenizerException {
		String termText=token.getTermText();
		termText=termText.replaceAll("[^A-Za-z0-9]", "");
		if(termText.equalsIgnoreCase("may") && isDate(termText))
			return stream.hasNext();
		if(stopWordList.contains(termText.toLowerCase()))
			stream.remove();
		return stream.hasNext();
	}
	private boolean isDate(String termText) {
		Token previousToken=stream.getPrevious();
		Token nextToken=stream.getNext();
		try
		{
			if(previousToken==null || nextToken==null)
				throw new TokenizerException("Found a null token");
			if(isNumber(previousToken.toString()) || isNumber(nextToken.toString()))
				return true;
			return false;
		}
		catch(TokenizerException e)
		{
		}
		return false;
	}

	private boolean isNumber(String token) {
		if(token.equals("") || token.equals(" "))
			return false;
		token=token.replaceAll("[.,?]", "");
		for (int i = 0; i < token.length(); i++) {
			if (!Character.isDigit(token.charAt(i)))
				return false;
		}
		return true;
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}
}
