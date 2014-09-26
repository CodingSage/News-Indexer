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
		Token token=new Token();
		token=stream.next();
		String termText=token.getTermText();
		Pattern p=Pattern.compile("^[A-Za-z]");
		Matcher m=p.matcher(termText);
		if(m.find())
		{
			Stemmer s=new Stemmer(termText);
			String modifiedTerm=s.externalStem();
			token.setTermText(modifiedTerm);
		}
		
		
		/*if(Character.isAlphabetic(termText.charAt(0))){
			termText=termText.replaceFirst("ness$", "");
			termText=termText.replaceFirst("ies$", "i");
			termText=termText.replaceFirst("s$", "");
			termText=termText.replaceFirst("eed$", "e");
			termText=termText.replaceFirst("ed$", "");
			termText=termText.replaceFirst("ted$", "t");
			termText=termText.replaceFirst("ing$", "");
			termText=termText.replaceFirst("y$", "i");
			termText=termText.replaceFirst("ional$", "");
			termText=termText.replaceFirst("ator$", "");
			termText=termText.replaceFirst("pic$", "p");
			termText=termText.replaceFirst("ement$", "");
			termText=termText.replaceFirst("ent$", "");
			termText=termText.replaceFirst("ness$", "");
			
			System.out.println("Modfied token : "+termText);
			token.setTermText(termText);
		}*/
		
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
