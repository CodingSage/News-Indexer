package edu.buffalo.cse.irf14.analysis;

public class NumberFilter extends TokenFilter {

	public NumberFilter(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token token = stream.next();
		try
		{
			if(token == null)
				throw new TokenizerException("Invalid token in analyse method in NumberFilter");
			return analyse(token);
		}catch(TokenizerException e){
			//System.out.println("Null token in NumberFilter");
		}
		if (stream.hasNext())
			return true;
		return false;
	}
	
	public boolean evaluateCurrent() throws TokenizerException{
	//	System.out.println("Evaluate Current : NumberFilter");
		Token token = stream.getCurrent();
		try
		{
			if(token == null)
				throw new TokenizerException("Invalid token in analyse method in NumberFilter");
			return analyse(token);
		}catch(TokenizerException e){
			//System.out.println("Null token in NumberFilter");
		}
		if (stream.hasNext())
			return true;
		return false;
		
	}

	private boolean analyse(Token token) throws TokenizerException {
		
		String termText = token.getTermText();
		if (isNumber(termText)) {
			termText = termText.replaceAll("[0-9,.]", "");
			if(termText.equals(""))
				stream.remove();
			else
				token.setTermText(termText);
		}
		if (stream.hasNext())
			return true;
		return false;
	}

	private boolean isNumber(String termText) {
		if (termText.matches("[0-9]*[,.//]*[0-9]*[%]*"))
			return true;
		return false;
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

}
