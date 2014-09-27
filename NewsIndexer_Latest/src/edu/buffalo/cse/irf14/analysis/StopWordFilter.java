package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StopWordFilter extends TokenFilter{
	static String []stopWords={"a","an","and","are","as","at","be","but","by","for","if","in","into","is","it","no","not","of","on","or","s","such","t","that","the","their","then","there","these",
		"they","this","to","was","will","with"};
	String stopWordPattern="^(a|able|about|across|after|all|almost|am(?:ong)?|an(?:d)?|any|are|as|at|be(?:cause)?|been|but|by|can(?:not)?|could|dear|did|do(?:es)?|either|"
			+ "else|ever(?:y)?|for|from|get|got|had|has|have|he(?:r)?|hers|him|his|how(?:ever)?|i|if|in(?:to)?|is|it(?:s)?|just|least|let|like(?:ly)?|may|me|might|most|must|my|"
			+ "neither|no(?:t)?|nor|of(?:f)?|often|on(?:ly)?|or|other|our|own|rather|said|say(?:s)?|she|should|since|so(?:me)?|such|than|that|the|their|them|then|there|these|they|this|tis|"
			+ "to(?:o)?"
			+ "|twas|us|wants|was|we(?:re)?|what|when|where|which|while|who(?:m)?|why|will|with|would|yet|you(?:r)?)$";

	public StopWordFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token token=stream.next();
		try
		{
			if(token == null)
				throw new TokenizerException("Invalid token in analyse method in StopwordFilter");
			return analyse(token);
		}catch(TokenizerException e)
		{//System.out.println("Null token in StopWordFilter");
			}
		

		if(stream.hasNext())
			return true;
		else
			return false;
	}


	public boolean evaluateCurrent() throws TokenizerException{
	//	System.out.println("Evaluate Current : StopWordFilter");
		Token token = stream.next();
		try
		{
			if(token == null)
				throw new TokenizerException("Invalid token in analyse method in StopwordFilter");
			return analyse(token);
		}catch(TokenizerException e)
		{//System.out.println("Null token in StopWordFilter");
		}

		if(stream.hasNext())
			return true;
		return false;
	}

	private boolean analyse(Token token) throws TokenizerException {

		Pattern p = Pattern.compile(stopWordPattern, Pattern.CASE_INSENSITIVE);
		Matcher m = null;
		boolean b = false;
		String termText=token.getTermText();
		termText=termText.replaceAll("[^A-Za-z0-9]", "");
		if(termText.equalsIgnoreCase("may"))
		{
			if(isDate(termText))
			{
				if(stream.hasNext())
					return true;
				return false;
			}
		}
		m=p.matcher(termText);
		if(m.find())
			stream.remove();
		if(stream.hasNext())
			return true;
		return false;
	}
	private boolean isDate(String termText) {
		Token previousToken=stream.getPrevious();
		Token nextToken=stream.getNext();
		try
		{
			if(previousToken==null || nextToken==null)
				throw new TokenizerException("Found a null token");
			
			if(isNumber(previousToken.toString()) || isNumber(nextToken.toString()))
			{
				return true;
			}
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
		boolean flag = true;
		for (int i = 0; i < token.length(); i++) {
			if (!Character.isDigit(token.charAt(i)))
				flag = false;
		}

		return flag;
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

}
