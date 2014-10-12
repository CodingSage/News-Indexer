package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialCharacterFilter extends TokenFilter{
	public final static Pattern p = Pattern.compile("[^a-zA-Z0-9{.}{!}{,}{?}{\\-} ]", Pattern.CASE_INSENSITIVE);
	public SpecialCharacterFilter(TokenStream stream) {
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
		Matcher m = null;
		m = p.matcher(termText);
		try{
			if (m.find())
			{
				termText=termText.replaceAll("[^a-zA-Z0-9{.}{!}{,}{?}{\\-} ]", "");
				if(termText.contains("-"))
				{
					int position=termText.indexOf("-");
					if(position!=0 && position!=termText.length()-1)
					{
						char chBefore=termText.charAt(position-1);
						char chAfter=termText.charAt(position+1);
						if(Character.isDigit(chBefore) && Character.isDigit(chAfter))
						{}
						else
							termText=termText.replace("-", "");
					}
					else
						termText=termText.replace("-", "");				
				}
				token.setTermText(termText);
			}
		}catch(Exception e)
		{}
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}
}
