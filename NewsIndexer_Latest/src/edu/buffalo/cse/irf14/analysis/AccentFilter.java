
package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;

public class AccentFilter extends TokenFilter {

	public AccentFilter(TokenStream stream) {
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

		if(!Normalizer.isNormalized(token.getTermText(), Normalizer.Form.NFD)){
			token.setTermText(Normalizer.normalize(token.getTermText(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""));
		}
		return stream.hasNext();	
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

}
