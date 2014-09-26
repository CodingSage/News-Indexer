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
		// TODO Auto-generated method stub

		Pattern p = Pattern.compile(stopWordPattern, Pattern.CASE_INSENSITIVE);
		Matcher m = null;
		boolean b = false;
		Token token=new Token();
		String termText=new String();
		token=stream.next();
		termText=token.getTermText();
		System.out.println("Token is : "+termText);
		m=p.matcher(termText);
		if(m.find())
		{
			stream.remove();
		}
		if(stream.hasNext())
			return true;
		else
			return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return stream;
	}

}
