package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StemmerFilter extends TokenFilter {
	String pattern="";
	public StemmerFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		Token token=stream.next();
		try
		{
			if(token == null)
				throw new TokenizerException("Invalid token in analyse method in StemmerFilter");
			return analyse(token);
		}catch(TokenizerException e)
		{
			//System.out.println("NUll token in StemmerFilter");
		}
		if(stream.hasNext())
			return true;
		else
			return false;
	}

	public boolean evaluateCurrent() throws TokenizerException{
		//System.out.println("Evaluate Current : StemmerFilter");
		Token token = stream.getCurrent();
		try
		{
			if(token == null)
				throw new TokenizerException("Invalid token in analyse method in StemmerFilter");
			return analyse(token);
		}catch(TokenizerException e)
		{
			//System.out.println("NUll token in StemmerFilter");
		}
		if(stream.hasNext())
			return true;
		else
			return false;

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
