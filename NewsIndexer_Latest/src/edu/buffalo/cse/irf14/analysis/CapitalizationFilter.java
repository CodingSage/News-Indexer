
package edu.buffalo.cse.irf14.analysis;

public class CapitalizationFilter extends TokenFilter {

	public CapitalizationFilter(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO check beginning of sentence condition
		return analyse(stream.next());	
	}

	@Override
	public boolean evaluateCurrent() throws TokenizerException{
		return analyse(stream.getCurrent());	
	}

	private boolean analyse(Token token) throws TokenizerException {
		String term=token.getTermText();
		if(isAllSmall(term))
			return stream.hasNext();
		else if(term.toUpperCase().equals(term))
			return stream.hasNext();
		else if(isFirstWord() && !isNextCapital())
		{
			term=term.toLowerCase();
			token.setTermText(term);
			return stream.hasNext();
		}
		else 
		{
			if(term.charAt(term.length()-1)!='.' && term.charAt(term.length()-1)!=',' && term.charAt(term.length()-1)!='!')
			{
				Token nextToken = null;
				while(isNextCapital())
				{
					nextToken=stream.getNext();
					token.merge(nextToken);
					stream.removeNext();
					term=token.getTermText();
					if(term.charAt(term.length()-1)=='.' || term.charAt(term.length()-1)==',' ||term.charAt(term.length()-1)=='!')
						break;
				}
			}
		}
		return stream.hasNext();
	}

	private boolean isNextCapital() {
		Token nextToken=stream.getNext();
		if(nextToken==null)
			return false;
		String nextTerm=nextToken.getTermText();
		if(nextTerm!=null && !nextTerm.equals("") && !nextTerm.equals(" "))
		{
			if(Character.isUpperCase(nextTerm.charAt(0)))
				return true;
		}
		return false;
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

	/*private boolean toLowerCase(Token token){
		if (isFirstWord(token, stream) && !isUpperCaseWord(token)) {
			token.setTermText(token.toString());
			return true;
		}
		return false;
	}

	private boolean isUpperCaseWord(Token token) {
		String word = token.toString();
		return word.toUpperCase().equals(word);
	}

	private boolean isCamelCase(Token token) {
		char startChar = token.toString().toCharArray()[0];
		if (startChar <= 'Z' && startChar >= 'A')
			return true;
		return false;
	}*/
	private boolean isAllSmall(String term)
	{
		boolean flag=true;
		for(int i=0;i<term.length();i++)
		{
			if(!Character.isDigit(term.charAt(i)) && Character.isUpperCase(term.charAt(i)))
				flag=false;
		}
		return flag;
	}
	private boolean isFirstWord() {
		//System.out.println("Complex expression is : "+stream.toString().split(" ")[0].toString());
		Token previousToken=stream.getPrevious();
		if(previousToken!=null)
		{
			if(!previousToken.toString().equals("")){
				if(previousToken.toString().charAt(previousToken.toString().length()-1)=='.')
					return true;
				return false;	
			}

		}
		return true;
	}

}
