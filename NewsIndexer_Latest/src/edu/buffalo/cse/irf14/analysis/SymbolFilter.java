package edu.buffalo.cse.irf14.analysis;

public class SymbolFilter extends TokenFilter {

	final static String [][]commonContractions={{"ain't","am not"},
		{"aren't","are not"},
		{"can't","cannot"},	{"could've","could have"},{"coudn't","could not"},{"couldn't've","could not have"},
		{"didn't","did not"},{"doesn't","does not"},{"don't","do not"},
		{"'em","them"},
		{"hadn't","had not"},{"hadn't've","had not have"},{"hasn't","has not"},{"haven't","have not"},{"he'd","he had"},{"he'd've","he would have"},{"he'll","he will"},{"he's","he is"},
		{"how'd","how would"},{"how'll","how will"},{"how's","how is"},
		{"I'd","I would"},{"I'd've","I would have"},{"I'll","I will"},{"I'm","I am"},{"I've","I have"},{"isn't","is not"},{"it'd","it would"},{"it'd've","it would have"},{"it'll","it will"},
		{"it's","it is"},
		{"let's","let us"},
		{"ma'am","madam"},{"mightn't","might not"},{"might't've","might not have"},{"might've","might have"},{"mustn't","must not"},{"must've","must have"},
		{"needn't","need not"},{"not've","not have"},
		{"o'clock","of the clock"},
		{"put'em","put them"},
		{"shan't","shall not"},{"she'd","shw would"},{"she'd've","she would have"},{"she'll","she will"},{"she's","she is"},{"should've","should have"},{"shouldn't","should not"},
		{"shouldn't've","should not have"},
		{"that's","that is"},{"there'd","there would"},{"there'd've","there would have"},{"there're","there are"},{"there's","there is"},{"they'd","they would"},{"they'd've","they would have"},
		{"they'll","they will"},{"they're","they are"},{"they've","they have"},
		{"wasn't","was not"},{"we'd","we had"},{"we'd've","we would have"},{"we'll","we will"},{"we're","we are"},{"we've","we have"},{"weren't","were not"},{"what'll","what will"},
		{"what're","what are"},{"what's","what is"},{"what've","what have"},{"when's","when is"},{"where'd","where did"},{"where's","where is"},{"where've","where have"},{"who'd","who would"},
		{"who'll","who will"},{"who're","who are"},{"who's","who is"},{"who've","who have"},{"why'll","why will"},{"why're","why are"},{"why's","why is"},{"won't","will not"},
		{"would've","would have"},{"wouldn't","would not"},{"wouldn't've","would not have"},
		{"y'all","you all"},{"y'all'd've","you all should have"},{"you'd","you would"},{"you'd've","you would have"},{"you'll","you will"},{"you're","you are"},{"you've","you have"}};

	public SymbolFilter(TokenStream stream) {
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
		String termText=token.getTermText();
		if(termText.equals(" ") || termText.equals("") || termText.equals("\n"))
			stream.remove(); 
		if(termText.contains("."))
		{
			int count=termText.length() - termText.replace(".", "").length();
			if(count==1)
			{
				if(termText.charAt(termText.length()-1) == '.')
				{
					termText=termText.replace(".","");
					termText=termText.trim();
					token.setTermText(termText);
				}
			}
			else
			{
				if(termText.charAt(termText.length()-1) == '.')
				{
					StringBuffer tempTermText=new StringBuffer(termText);
					tempTermText.deleteCharAt(tempTermText.length()-1);
					termText=tempTermText.toString();
					termText=termText.trim();
					token.setTermText(termText);
					if(removeOthers(termText))
					{
						termText=termText.replaceAll("[.]", "");
						token.setTermText(termText);
					}
				}
			}
		}
		 if(termText.contains(","))
		{
			termText=termText.replaceAll(",","");
			token.setTermText(termText);
		}
		if(termText.contains("?"))
		{
			if((termText.indexOf("?") == termText.length()-1))
			{
				termText=termText.replace("?"," ");
				termText=termText.trim();
				token.setTermText(termText);
			}
			else if(checkMiddle(termText)){
				termText=termText.replace("?"," ");
				termText=termText.trim();
				token.setTermText(termText);
			}
		}
		if(termText.contains("!"))
		{
			if(termText.indexOf("!")!=0)
			{
				termText=termText.replace("!"," ");
				termText=termText.trim();
				token.setTermText(termText);
			}
		}
		if(termText.contains("-"))
		{
			if(termText.equals("-"))
				stream.remove();
			else if(termText.matches(".*\\d+.*"))
			{
			}
			else
			{
				termText=termText.replace("-", " ");
				termText=termText.trim();
				token.setTermText(termText);
			}
		}
		if(termText.contains("'"))
		{
			boolean isContraction=false;
			int contractionPosition=0;
			for(int j=0;j<commonContractions.length;j++)
			{
				if(termText.equalsIgnoreCase(commonContractions[j][0]))
				{
					isContraction=true;
					contractionPosition=j;
					break;
				}
			}
			if(isContraction)
			{
				boolean isCapital=false;
				if(Character.isUpperCase(termText.charAt(0)))
					isCapital=true;					
				termText=commonContractions[contractionPosition][1];
				token.setTermText(termText);
				if(isCapital)
				{
					char term[]=token.getTermBuffer();
					term[0]=Character.toUpperCase(term[0]);
					token.setTermBuffer(term);
				}

			}
			else
			{
				int pos=termText.indexOf("'");
				if(pos!=termText.length()-1)
				{
					if(termText.charAt(pos+1)=='s')
					{
						StringBuffer temp=new StringBuffer(termText);
						temp.deleteCharAt(pos+1);
						temp.deleteCharAt(pos);
						termText=temp.toString();
						token.setTermText(termText);
					}
					else
					{
						StringBuffer temp=new StringBuffer(termText);	
						temp.deleteCharAt(pos);
						termText=temp.toString();
						token.setTermText(termText);
					}
				}
				else
				{
					StringBuffer temp=new StringBuffer(termText);	
					temp.deleteCharAt(pos);
					termText=temp.toString();
					token.setTermText(termText);
				}
				termText=termText.replaceAll("'","");
				token.setTermText(termText);
			}
		}
		return stream.hasNext();

	}

	private boolean removeOthers(String termText) {
		String termTextParts[]=termText.split(".");
		boolean fullNumber=true;
		boolean allCaps=true;
		for(int i=0;i<termTextParts.length;i++)
		{
			if(!isNumber(termTextParts[i]))
			{
				fullNumber=false;
				break;
			}
		}
		if(!fullNumber)
		{
			for(int i=0;i<termTextParts.length;i++)
			{
				if(termTextParts[i].length()==1 && Character.isUpperCase(termTextParts[i].charAt(0)))
					continue;
				else
					allCaps=false;
			}
		}
		else
			return true;
		if(!allCaps)
			return true;
		return false;
	}

	private boolean isNumber(String string) {
		for(int i=0;i<string.length();i++)
		{
			if(!Character.isDigit(string.charAt(i)))
				return false;
		}
		return true;
	}

	private boolean checkMiddle(String termText) {
		int position=termText.indexOf("?");
		if(position!=0 && position!=termText.length()-1)
		{
			char before=termText.charAt(position-1);
			char after=termText.charAt(position+1);
			if(Character.isAlphabetic(before) && Character.isAlphabetic(after))
				return false;
		}
		return true;
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}
}
