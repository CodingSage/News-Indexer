package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialCharacterFilter extends TokenFilter{

	public SpecialCharacterFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		Pattern p = Pattern.compile("[^a-zA-Z0-9{.}{!}{,}{?}{\\-} ]", Pattern.CASE_INSENSITIVE);
		Matcher m = null;
		Token token=new Token();
		String termText=new String();

		token=stream.next();
		termText=token.getTermText();

		m = p.matcher(termText);
		if (m.find())
		{
			termText=termText.replaceAll("[^a-zA-Z0-9{.}{!}{,}{?}{\\-} ]", " ");
			termText=termText.trim();
			if(termText.contains("-"))
			{
				System.out.println("Found -");
				int position=termText.indexOf("-");
				char chBefore=termText.charAt(position-1);
				char chAfter=termText.charAt(position+1);
				if(Character.isDigit(chBefore) && Character.isDigit(chAfter))
				{}
				else //if(Character.isAlphabetic(chBefore) && Character.isAlphabetic(chAfter))
					termText=termText.replace("-", "");
			}
			//eliminateSpaces(termText);
			termText=termText.replaceAll(" ", "");
			token.setTermText(termText);
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
