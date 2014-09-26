package edu.buffalo.cse.irf14.analysis;

public class CapitalizationFilter extends TokenFilter {

	public CapitalizationFilter(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO check beginning of sentence condition
		Token token = stream.next();
		String term=token.getTermText();
		if(isAllSmall(term))
		{
			System.out.println("Term that is allSmall is : "+term);
			//return true;
			if(stream.hasNext())
				return true;
			else
				return false;
		}

		if(token.toString().toUpperCase().equals(token.toString()))
		{
			//return true;
			if(stream.hasNext())
				return true;
			else
				return false;
		}
		if(isFirstWord(token, stream))
		{
			term=term.toLowerCase();
			token.setTermText(term);
			System.out.println(term);
			//return true;
			if(stream.hasNext())
				return true;
			else
				return false;
		}
		System.out.println("failed all above tests for token : "+token.toString());
		Token nextToken = stream.next();
		if(nextToken!=null)
		{
			boolean t1 = !toLowerCase(token) && (isCamelCase(token) && !isFirstWord(token, stream));
			boolean t2 = !toLowerCase(nextToken) && (isCamelCase(nextToken) && !isFirstWord(nextToken, stream));
			System.out.println("t1 "+t1+" and t2 : "+t2);
			if (t1 && t2) {
				System.out.println("Merging tokens "+token.toString()+" and "+nextToken.toString());
				token.merge(nextToken);
				stream.remove();
				//nextToken = stream.next();
				System.out.println("Current token is "+token.getTermText());
			}
		}
		else
		{
			//return true;
			if(stream.hasNext())
				return true;
			else
				return false;
		}
		//System.out.println("Got next token "+nextToken.toString());

		if(stream.hasNext())
			return true;
		else
			return false;
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

	private boolean toLowerCase(Token token){
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
	}
	private boolean isAllSmall(String term)
	{
		boolean flag=true;
		for(int i=0;i<term.length();i++)
		{
			if(Character.isUpperCase(term.charAt(i)))
				flag=false;
		}
		return flag;
	}
	private boolean isFirstWord(Token token, TokenStream stream) {
		//System.out.println("Complex expression is : "+stream.toString().split(" ")[0].toString());
		Token previousToken=stream.getPrevious();
		if(previousToken!=null)
		{
			if(previousToken.toString().charAt(previousToken.toString().length()-1)=='.')
				return true;
			else
				return false;
		}
		else
		{
			return true;
		}
	}

}
