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
		String termText=token.getTermText();
		/*String modifiedToken=Normalizer.normalize(termText, Normalizer.Form.NFD);
		modifiedToken= modifiedToken.replaceAll("[^\\p{ASCII}]", "");
		System.out.println(modifiedToken);*/
		termText=Normalizer.normalize(termText, Normalizer.Form.NFD);
		termText= termText.replaceAll("[^\\p{ASCII}]", "");
		System.out.println(termText);
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
