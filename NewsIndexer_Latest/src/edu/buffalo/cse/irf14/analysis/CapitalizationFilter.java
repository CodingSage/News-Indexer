package edu.buffalo.cse.irf14.analysis;

public class CapitalizationFilter extends TokenFilter {

	public CapitalizationFilter(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO check beginning of sentence condition
		if (stream.toString().toUpperCase().equals(stream.toString()))
			return false;
		Token token = stream.next();
		do {
			Token nextToken = stream.next();
			boolean t1 = !toLowerCase(token) && (isCamelCase(token) && !isFirstWord(token, stream));
			boolean t2 = !toLowerCase(nextToken) && (isCamelCase(nextToken) && !isFirstWord(nextToken, stream));
			if (!t1 && !t2) {
				token.merge(nextToken);
				stream.remove();
				nextToken = stream.next();
			}
			token = nextToken;
		} while (token != null);
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

	private boolean isFirstWord(Token token, TokenStream stream) {
		if (stream.toString().split(" ")[0].equals(token.toString()))
			return true;
		return false;
	}

}
