
package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;

public class AccentFilter extends TokenFilter {

	public AccentFilter(TokenStream stream) {
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
				throw new TokenizerException("Invalid token in analyse method in AccentFilter");
			return analyse(token);
		}catch(TokenizerException e)
		{//System.out.println("Null token in AccentFilter");
		}
		
		if(stream.hasNext())	
			return true;
		else
			return false;
	}
	
	public boolean evaluateCurrent() throws TokenizerException{
		Token token = stream.getCurrent();
		try
		{
			if(token == null)
				throw new TokenizerException("Invalid token in analyse method in AccentFilter");
			return analyse(token);
		}catch(TokenizerException e)
		{//System.out.println("Null token in AccentFilter");
		}
		if(stream.hasNext())	
			return true;
		else
			return false;
		
	}

	private boolean analyse(Token token) throws TokenizerException {
	
		String termText=token.getTermText();
		termText=Normalizer.normalize(termText, Normalizer.Form.NFD);
		termText= termText.replaceAll("[^\\p{ASCII}]", "");
		token.setTermText(termText);
		
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
