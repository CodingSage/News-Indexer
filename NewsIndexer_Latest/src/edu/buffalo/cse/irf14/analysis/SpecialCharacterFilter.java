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
		Token token=stream.next();
		try
		{
			if(token == null)
				throw new TokenizerException("Invalid token in analyse method in SpecialCharacterFilter");
			return analyse(token);
		}
		catch(TokenizerException e)
		{//System.out.println("Null token in SpecialCharacterFilter");
		}
		if(stream.hasNext())
			return true;
		else
			return false;
	}	
	
	public boolean evaluateCurrent() throws TokenizerException{
		//System.out.println("Evaluate Current : SpecialCharacterFilter");
		Token token = stream.getCurrent();
		try
		{
			if(token == null)
				throw new TokenizerException("Invalid token in analyse method in SpecialCharacterFilter");
			return analyse(token);
		}
		catch(TokenizerException e)
		{//System.out.println("Null token in SpecialCharacterFilter");
		}
		if(stream.hasNext())
			return true;
		else
			return false;
		
	}

	private boolean analyse(Token token) throws TokenizerException {
		String termText=token.getTermText();
		Pattern p = Pattern.compile("[^a-zA-Z0-9{.}{!}{,}{?}{\\-} ]", Pattern.CASE_INSENSITIVE);
		Matcher m = null;
		m = p.matcher(termText);
		if (m.find())
		{
			termText=termText.replaceAll("[^a-zA-Z0-9{.}{!}{,}{?}{\\-} ]", " ");
			termText=termText.trim();
			if(termText.contains("-"))
			{
				int position=termText.indexOf("-");
				if(position!=0 && position!=termText.length()-1)
				{
					char chBefore=termText.charAt(position-1);
					char chAfter=termText.charAt(position+1);
					if(Character.isDigit(chBefore) && Character.isDigit(chAfter))
					{}
					else //if(Character.isAlphabetic(chBefore) && Character.isAlphabetic(chAfter))
						termText=termText.replace("-", "");
				}
				else
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
