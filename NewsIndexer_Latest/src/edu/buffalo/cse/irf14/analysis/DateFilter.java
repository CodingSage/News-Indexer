package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFilter extends TokenFilter {
	String[][] months = { { "January", "01" }, { "February", "02" },
			{ "March", "03" }, { "April", "04" }, { "May", "05" },
			{ "June", "06" }, { "July", "07" }, { "August", "08" },
			{ "September", "09" }, { "October", "10" }, { "November", "11" },
			{ "December", "12" } };
	String yyyymmddDatePattern = "^(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$";
	String ddmmyyyyDatePattern = "(0[1-9]|[12][0-9]|3[01])[.-/](0[1-9]|1[012])[- /.](19|20)\\d\\d";
	String mmddyyyyDatePattern = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$";
	String monthPattern = "^(?:jan(?:uary)?|feb(?:ruary)?|march|mar|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|(nov|dec)(?:ember)?)$";  //mar(?:ch)?
	String dayPattern = "(sun|mon|tue|wednes|thurs|fri|satur)day";
	String hhmmssPattern = "(0[1-9]|1[0-2]):([0-5][0-9]:[0-5][0-9]|(59|44|29):60) (AM|am|PM|pm|Am|aM|Pm|pM)";
	String hhmmPattern = "([0-9]|0[0-9]|[1][0-2]):([0-5]\\d)\\s*(?:AM|am|PM|pm|Am|aM|Pm|pM)[,.\\s]*";
	String time24HourPattern = "([0-9]|0[0-9]|1?[0-9]|2[0-3]):[0-5]?[0-9]"; // from 00:00 to 23:59
	String time24WithSecondsHourPattern = "(?:2[0-3]|[01]?[0-9]):[0-5][0-9]:[0-5][0-9]"; // from 00:00:00 to 23:59:59
	String time24WithSecondsHourPatternTimeZone = "(?:2[0-3]|[01]?[0-9]):[0-5][0-9]:[0-5][0-9][A-Za-z]{3}"; // from 00:00:00 to 23:59:59
	String complexPattern = "(?:2[0-3]|[01]?[0-9]):[0-5][0-9]:[0-5][0-9].*(sun|mon|tue|wednes|thurs|fri|satur)day.*(0[1-9]|[12][0-9]|3[01]).*"
			+ "(?:jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|(nov|dec)(?:ember)?).*(19|20)\\d\\d";
	String dateMonthnameYearPattern = "(0[1-9]|[12][0-9]|3[01]) (?:jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|(nov|dec)(?:ember)?) (19|20)\\d\\d";
	String monthnameDateYearPattern = "(?:jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|(nov|dec)(?:ember)?) (0[1-9]|[12][0-9]|3[01]),//s*(19|20)\\d\\d";
	String monthnameDatePattern = "(?:jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|(nov|dec)(?:ember)?) (0[1-9]|[12][0-9]|3[01])";

	public DateFilter(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token token=stream.next();
		try
		{
			if(token == null)
				throw new TokenizerException();
			return analyse(token);	
		}catch(TokenizerException e)
		{
			//System.out.println("CapitalFilter");
		}
		
		return stream.hasNext();	}

	public boolean evaluateCurrent() throws TokenizerException{
		Token token=stream.getCurrent();
		try
		{
			if(token == null)
				throw new TokenizerException();
			return analyse(token);	
		}catch(TokenizerException e)
		{
			//System.out.println("CapitalFilter");
		}
		
		return stream.hasNext();	}

	private boolean analyse(Token token) throws TokenizerException {
		try
		{
			Pattern p = Pattern.compile(monthPattern, Pattern.CASE_INSENSITIVE);
			Matcher m = null;
			String termText = token.getTermText();
			if (termText.matches(yyyymmddDatePattern)) {
				String[] splitDate = termText.split(getDivider(termText));
				termText = splitDate[2] + splitDate[1] + splitDate[0];
				token.setTermText(termText);
			}
			else if (termText.matches(ddmmyyyyDatePattern)) {
				String[] splitDate = termText.split(getDivider(termText));
				termText = splitDate[2] + splitDate[1] + splitDate[0];
				token.setTermText(termText);
			}
			else if (termText.matches(mmddyyyyDatePattern)) {
				String[] splitDate = termText.split(getDivider(termText));
				termText = splitDate[2] + splitDate[0] + splitDate[1];
				token.setTermText(termText);
			}
			else if (isNumber(termText)) {
				if (Integer.parseInt(termText) <= 31) // If the single number found less than 31 then it is a date and might be followed by Month and  Year
				{
					Token nextToken = stream.getNext();
					if(nextToken==null)
					{
						if(stream.hasNext())
							return true;
						return false;
					}
					String nextTerm = nextToken.toString();
					m = p.matcher(nextTerm);
					if (m.find()) {
						String date = termText;
						String month = nextTerm;
						String monthNumber = new String();
						token.merge(nextToken);
						stream.removeNext();
						nextToken = stream.getNext();
						String year = nextToken.toString();
						token.merge(nextToken);
						stream.remove();
						String finalTerm = new String();
						finalTerm += year;
						for (int j = 0; j < months.length; j++) {
							if (month.equalsIgnoreCase(months[j][0])) {
								monthNumber = months[j][1];
								break;
							}
						}
						finalTerm += monthNumber;
						if (Integer.parseInt(date) < 10)
							finalTerm = finalTerm + "0" + date;
						else
							finalTerm += date;
						token.setTermText(finalTerm);
					}
				}
				else
				{
					Token nextToken = stream.getNext();
					if(nextToken!=null)
					{
						String nextTerm = nextToken.toString();
						if ((nextTerm.equalsIgnoreCase("ad") || nextTerm.equalsIgnoreCase("bc")) && stream.hasNext()) // 878 BC
						{
							nextToken = stream.getNext();
							token.merge(nextToken);
							stream.removeNext();
							String finalTerm = new String();
							if (nextTerm.equalsIgnoreCase("bc"))
								finalTerm += "-";
							Integer temp = Integer.parseInt(termText);
							if (temp < 100)
								finalTerm += "00" + termText + "0101";
							else if (temp < 1000)
								finalTerm += "0" + termText + "0101";
							else
								finalTerm += termText + "0101";
							token.setTermText(finalTerm);
							//nextToken = stream.next();
						}
						else if(Integer.parseInt(termText)<5000 && Integer.parseInt(termText)>1000) 			//Probably just a year like 1948
						{
							Integer temp = Integer.parseInt(termText);
							String finalTerm = "";
							if (temp < 100)
								finalTerm += "00" + termText + "0101";
							else if (temp < 1000)
								finalTerm += "0" + termText + "0101";
							else
								finalTerm += termText + "0101";
							token.setTermText(finalTerm);
							nextToken = stream.next();
						}
					}
				}
			}
			else if (isCombinedBCAD(termText)) // 847AD
			{
				int year = 0;
				boolean started = false;
				for (int i = 0; i < termText.length(); i++) {
					if (Character.isDigit(termText.charAt(i)))
						year = year * 10
						+ Integer.parseInt(termText.substring(i, i + 1));
					else
						break;
				}
				String tempText = termText.toLowerCase();
				if (tempText.contains("ad")) {
					if (year < 100)
						termText = "00" + year + "0101";
					else if (year < 1000)
						termText = "0" + year + "0101";
					else
						termText = "" + year + "0101";
				} 
				else {
					if (year < 100)
						termText = "-00" + year + "0101";
					else if (year < 1000)
						termText = "-0" + year + "0101";
					else
						termText = "-" + year + "0101";
				}
				token.setTermText(termText);
			} 
			else if (termText.matches(time24HourPattern)) // time24HourPattern="([0-9]|0[0-9]|1?[0-9]|2[0-3]):[0-5]?[0-9]"; from 00:00 to 23:59
			{
				Token nextToken = stream.getNext();
				String nextTerm = nextToken.toString();
				nextTerm = nextTerm.replaceAll("[^a-zA-Z]", "");
				if (nextTerm.equalsIgnoreCase("am")) {
					nextToken = stream.getNext();
					token.merge(nextToken);
					stream.removeNext();
					termText = termText + ":00";
					token.setTermText(termText);
					nextToken = stream.next();
				} 
				else if (nextTerm.equalsIgnoreCase("pm")) {
					nextToken = stream.getNext();
					token.merge(nextToken);
					stream.removeNext();
					String[] temp = termText.split(":");
					Integer hour = Integer.parseInt(temp[0]);
					if (hour < 12)
						hour += 12;
					termText += hour + ":" + temp[1] + ":00";
					token.setTermText(termText);
					nextToken = stream.next();
				}
			} 
			else if (termText.matches(hhmmPattern)) {
				if (termText.contains("am") || termText.contains("aM")
						|| termText.contains("Am") || termText.contains("AM")) {
					termText = termText.replaceAll("[A-Za-z]", "");
					termText = termText + ":00";
					token.setTermText(termText);
				} 
				else if (termText.contains("pm") || termText.contains("pM")|| termText.contains("Pm") || termText.contains("PM")) {
					termText = termText.replaceAll("[A-Za-z]", "");
					termText = termText.replaceAll("[^0-9:]", "");
					String[] temp = termText.split(":");
					Integer hour = Integer.parseInt(temp[0]);
					if (hour < 12)
						hour += 12;
					termText = hour + ":" + temp[1] + ":00";
					token.setTermText(termText);
				}
			} 
			else if (termText.contains("-")) {
				if(termText.length()<6)
				{
					stream.remove();
					stream.next();
				}
				else{
					String[] temp = termText.split("-");
					temp[1] = temp[1].replaceAll("[^0-9]*", "");
					if (isNumber(temp[0]) && isNumber(temp[1])) {
						if(Integer.parseInt(temp[0])>1800)
						{
							if (temp[1].length() < temp[0].length()) {
								temp[1] = temp[0].substring(0, 2) + temp[1];
							}
							termText = temp[0] + "0101-" + temp[1] + "0101";
							token.setTermText(termText);
						}
						else
						{
							stream.remove();
							stream.next();
						}
					}
				}
			} 
			else 
			{
				m = p.matcher(termText); // Matching a month for the next else-if
				if (termText.matches(time24WithSecondsHourPatternTimeZone)) // this checks for the complex part with the UTC
				{
					termText = termText.replaceAll("[A-Za-z]", "");
					token.setTermText(termText);
					Token rest = stream.getRest();
					String restTerms = rest.toString();
					Pattern dmy = Pattern.compile(dateMonthnameYearPattern,
							Pattern.CASE_INSENSITIVE);
					Pattern mdy = Pattern.compile(monthnameDateYearPattern,
							Pattern.CASE_INSENSITIVE);
					Pattern md = Pattern.compile(monthnameDatePattern,
							Pattern.CASE_INSENSITIVE);
					Matcher dmyMatcher = dmy.matcher(restTerms), mdyMatcher = mdy
							.matcher(restTerms), mdMatcher = md.matcher(restTerms);
					if (dmyMatcher.find()) {
						int pos = restTerms.indexOf(dmyMatcher.group());
						if (restTerms.contains(".")) {
							if (pos < restTerms.indexOf(".") && pos < 20) // Take 20 as the limit of number of characters to allow it to be one date.
							{
								String[] date = dmyMatcher.group().split(" ");
								Token nextToken = new Token();
								nextToken = stream.getNext();
								while (!nextToken.toString().equals(date[0])) {
									stream.removeNext();
									nextToken = stream.getNext();
								}
								token.merge(nextToken);
								stream.removeNext();
								nextToken = stream.getNext();
								token.merge(nextToken);
								stream.removeNext();
								nextToken = stream.getNext();
								token.merge(nextToken);
								stream.removeNext();
								nextToken = stream.getNext();
								String finalDate[] = token.toString().split(" ");
								String finalTerm = finalDate[3];
								String monthNumber = new String();
								for (int j = 0; j < months.length; j++) {
									if (finalDate[2].equalsIgnoreCase(months[j][0])) {
										monthNumber = months[j][1];
										break;
									}
								}
								finalTerm += monthNumber;
								if (Integer.parseInt(finalDate[1]) < 10)
									finalTerm = finalTerm + "0" + finalDate[1];
								else
									finalTerm += finalDate[1];
								finalTerm += " " + finalDate[0];
								token.setTermText(finalTerm);
							}
						} else if (pos < 20) {
							String[] date = dmyMatcher.group().split(" ");
							Token nextToken = new Token();
							nextToken = stream.next();
							while (!nextToken.toString().equals(date[0])) {
								stream.remove();
								nextToken = stream.getNext();
							}
							token.merge(nextToken);
							stream.removeNext();
							nextToken = stream.getNext();
							token.merge(nextToken);
							stream.removeNext();
							nextToken = stream.getNext();
							token.merge(nextToken);
							stream.removeNext();
							nextToken = stream.getNext();
							String finalDate[] = token.toString().split(" ");
							String finalTerm = finalDate[3];
							String monthNumber = new String();
							for (int j = 0; j < months.length; j++) {
								if (finalDate[2].equalsIgnoreCase(months[j][0])) {
									monthNumber = months[j][1];
									break;
								}
							}
							finalTerm += monthNumber;
							if (Integer.parseInt(finalDate[1]) < 10)
								finalTerm = finalTerm + "0" + finalDate[1];
							else
								finalTerm += finalDate[1];
							finalTerm += " " + finalDate[0];
							token.setTermText(finalTerm);
						}
					}

					else if (mdyMatcher.find()) {} 
					else if (mdMatcher.find()) {}
				} 
				else if (m.find()) {
					String month = termText;
					Token nextToken = stream.getNext();
					if(nextToken==null)				//Just a month
					{
						String finalTerm = new String();
						String monthNumber = new String();
						for (int j = 0; j < months.length; j++) {
							if (month.equalsIgnoreCase(months[j][0])) {
								monthNumber = months[j][1];
								break;
							}
						}
						finalTerm="1900"+monthNumber+"01";
						token.setTermText(finalTerm);
						if(stream.hasNext())
							return true;
						return false;
					}
					String nextTerm = nextToken.toString();
					if (isDate(nextTerm)) // Maybe just a month by itself followed by some arbitrary words. Check if the next token is a number.
					{
						Token nextToNextToken = stream.getNextToNext();
						String nextToNextTerm = new String();
						if (nextToNextToken != null) // Might be followed by year
						{
							nextToNextTerm = nextToNextToken.getTermText();
							if (isYear(nextToNextTerm)) // Format is month date year
							{
								nextToken = stream.getNext();
								token.merge(nextToken);
								String date = nextTerm;
								date = date.replaceAll("[,]*", "");
								stream.removeNext();
								nextToken = stream.getNext();

								nextTerm = nextToken.toString();
								nextTerm = nextTerm.replaceAll("[,]*", "");

								String year = nextTerm;
								token.merge(nextToken);
								stream.removeNext();
								String finalTerm = new String();
								finalTerm += year;
								String monthNumber = new String();
								for (int j = 0; j < months.length; j++) {
									if (month.equalsIgnoreCase(months[j][0])) {
										monthNumber = months[j][1];
										break;
									}
								}
								finalTerm += monthNumber;
								if (Integer.parseInt(date) < 10)
									finalTerm = finalTerm + "0" + date;
								else
									finalTerm += date;
								token.setTermText(finalTerm);
							} 
							else // Format is month date
							{
								nextToken = stream.getNext();
								token.merge(nextToken);
								String date = nextTerm;
								date = date.replaceAll("[,]*", "");
								stream.removeNext();
								String finalTerm = "1900";
								String monthNumber = new String();
								for (int j = 0; j < months.length; j++) {
									if (month.equalsIgnoreCase(months[j][0])) {
										monthNumber = months[j][1];
										break;
									}
								}
								finalTerm += monthNumber;
								date=date.replaceAll("[^0-9]", "");
								if (Integer.parseInt(date) < 10)
									finalTerm = finalTerm + "0" + date;
								else
									finalTerm += date;
								token.setTermText(finalTerm);
							}
						}
						else // Format is month date
						{
							nextToken = stream.getNext();
							token.merge(nextToken);
							String date = nextTerm;
							date = date.replaceAll("[,]*", "");
							stream.removeNext();
							String finalTerm = "1900";
							String monthNumber = new String();
							for (int j = 0; j < months.length; j++) {
								if (month.equalsIgnoreCase(months[j][0])) {
									monthNumber = months[j][1];
									break;
								}
							}
							finalTerm += monthNumber;
							date=date.replaceAll("[^0-9]", "");
							if (Integer.parseInt(date) < 10)
								finalTerm = finalTerm + "0" + date;
							else
								finalTerm += date;
							token.setTermText(finalTerm);
						}
					}
				}
			}
			return stream.hasNext();	
		}catch(NumberFormatException e)
		{
			stream.remove();
		}
		finally
		{
			return stream.hasNext();
		}
	}

	private String getDivider(String termText) {
		if (termText.contains("/"))
			return "/";
		else if (termText.contains("."))
			return ".";
		else if (termText.contains("-"))
			return "-";

		return null;
	}

	private boolean isYear(String term) {
		term = term.replaceAll("[^0-9]*", "");
		if (term.matches("(11|12|13|14|15|16|17|18|19|20)\\d\\d"))
			return true;
		return false;
	}

	private boolean isDate(String term) {
		term = term.replaceAll("[^0-9]*", "");
		if (term.matches("([1-9]|0[1-9]|[1-2][0-9]|3[01])"))
			return true;
		return false;
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

	private boolean isNumber(String token) {
		if(token.equals("") || token.equals(" "))
			return false;
		boolean flag = true;
		for (int i = 0; i < token.length(); i++) {
			if (!Character.isDigit(token.charAt(i)))
				flag = false;
		}

		return flag;
	}

	private boolean isCombinedBCAD(String termText) {
		if (termText.contains("BC") || termText.contains("AD")
				|| termText.contains("bc") || termText.contains("ad")) {
			if (containsNumbers(termText))
				return true;
		}
		return false;
	}

	private boolean containsNumbers(String termText) {
		for (int i = 0; i < termText.length(); i++) {
			if (Character.isDigit(termText.charAt(i)))
				return true;
		}
		return false;
	}

}
