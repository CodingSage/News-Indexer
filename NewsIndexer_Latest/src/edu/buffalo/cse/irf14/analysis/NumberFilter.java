package edu.buffalo.cse.irf14.analysis;

public class NumberFilter extends TokenFilter {

	public NumberFilter(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token token = stream.next();
		return analyse(token);
	}
	
	public boolean current() throws TokenizerException{
		Token token = stream.getCurrent();
		return analyse(token);
	}

	private boolean analyse(Token token) {
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
