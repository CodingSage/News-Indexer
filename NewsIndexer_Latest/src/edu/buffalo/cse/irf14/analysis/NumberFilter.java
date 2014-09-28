package edu.buffalo.cse.irf14.analysis;

public class NumberFilter extends TokenFilter {

	public NumberFilter(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		//Token token=stream.next();
		return analyse(stream.next());	
	}
	public boolean evaluateCurrent() throws TokenizerException{
		//Token token=stream.getCurrent();
		return analyse(stream.getCurrent());	
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
		return stream.hasNext();
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
