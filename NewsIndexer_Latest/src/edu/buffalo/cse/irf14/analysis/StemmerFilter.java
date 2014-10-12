package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StemmerFilter extends TokenFilter {
	String pattern="";
	public StemmerFilter(TokenStream stream) {
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
		Pattern p=Pattern.compile("^[A-Za-z]");
		Matcher m=p.matcher(termText);
		if(m.find())
		{
			Stemmer s=new Stemmer(termText);
			String modifiedTerm=s.externalStem();
			token.setTermText(modifiedTerm);
		}
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

}
