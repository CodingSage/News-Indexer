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
	String monthPattern = "(?:jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|(nov|dec)(?:ember)?)\\D";
	String dayPattern = "(sun|mon|tue|wednes|thurs|fri|satur)day";
	String hhmmssPattern = "(0[1-9]|1[0-2]):([0-5][0-9]:[0-5][0-9]|(59|44|29):60) (AM|am|PM|pm|Am|aM|Pm|pM)";
	String hhmmPattern = "([0-9]|0[0-9]|[1][0-2]):([0-5]\\d)\\s*(?:AM|am|PM|pm|Am|aM|Pm|pM)";
	String time24HourPattern = "([0-9]|0[0-9]|1?[0-9]|2[0-3]):[0-5]?[0-9]"; // from
	// 00:00
	// to
	// 23:59
	String time24WithSecondsHourPattern = "(?:2[0-3]|[01]?[0-9]):[0-5][0-9]:[0-5][0-9]"; // from
	// 00:00:00
	// to
	// 23:59:59
	String time24WithSecondsHourPatternTimeZone = "(?:2[0-3]|[01]?[0-9]):[0-5][0-9]:[0-5][0-9][A-Za-z]{3}"; // from
	// 00:00:00
	// to
	// 23:59:59
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
		Pattern p = Pattern.compile(monthPattern, Pattern.CASE_INSENSITIVE);
		Matcher m = null;
		Token token = stream.next();
		String termText = token.getTermText();
		System.out.println("starting : " + termText);
		if (termText.matches(yyyymmddDatePattern)) {
			System.out.println("Updated token is : " + termText);
			token.setTermText(termText);
		}

		else if (termText.matches(ddmmyyyyDatePattern)) {
			String divider = getDivider(termText);
			String[] splitDate = termText.split(divider);
			termText = splitDate[2] + splitDate[1] + splitDate[0];
			token.setTermText(termText);

		}

		else if (termText.matches(mmddyyyyDatePattern)) {
			String divider = getDivider(termText);
			String[] splitDate = termText.split(divider);
			termText = splitDate[2] + splitDate[0] + splitDate[1];
			token.setTermText(termText);
		}

		else if (isNumber(termText)) {
			if (Integer.parseInt(termText) <= 31) // If the single number found
			// less than 31 then it is a
			// date and might be
			// followed by Month and
			// Year
			{
				System.out.println(termText + "--->is a date");
				Token nextToken = stream.next();
				String nextTerm = nextToken.toString();
				m = p.matcher(nextTerm);
				if (m.find()) {
					String date = termText;
					System.out.println("Found month");
					String month = nextTerm;
					String monthNumber = new String();
					token.merge(nextToken);
					System.out
							.println("Current token is : " + token.toString());
					stream.remove();
					nextToken = stream.next();
					String year = nextToken.toString();
					System.out.println("Next token is : "
							+ nextToken.toString());
					System.out.println("Found year");
					token.merge(nextToken);
					System.out
							.println("Current token is : " + token.toString());
					stream.remove();
					// System.out.println("***************NEXXT TOKEN IS : "+stream.getNext().toString());
					// nextToken=stream.next();
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
					System.out.println("Final date is : " + finalTerm);
					token.setTermText(finalTerm);
					System.out.println("Current token is : "
							+ token.getTermText());
				}
			}

			else {
				Token nextToken = stream.getNext();
				String nextTerm = nextToken.toString();
				if (nextTerm.equalsIgnoreCase("ad")
						|| nextTerm.equalsIgnoreCase("bc")) // 878 BC
				{
					nextToken = stream.next();
					System.out.println("Temp token is " + nextToken.toString());
					System.out.println(termText + "--->Found a BC/AD date");
					token.merge(nextToken);
					stream.remove();
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
					System.out.println("Final date is : " + finalTerm);
					token.setTermText(finalTerm);
					nextToken = stream.next();
				}

				else if(Integer.parseInt(termText)<5000 && Integer.parseInt(termText)>1000) 			//Probably just a year like 1948
				{
					System.out.println(termText + "--->Found an year only");
					Integer temp = Integer.parseInt(termText);
					String finalTerm = "";
					if (temp < 100)
						finalTerm += "00" + termText + "0101";
					else if (temp < 1000)
						finalTerm += "0" + termText + "0101";
					else
						finalTerm += termText + "0101";
					System.out.println("Final date is : " + finalTerm);
					token.setTermText(finalTerm);
					nextToken = stream.next();
				}

			}

		} else if (isCombinedBCAD(termText)) // 847AD
		{
			int year = 0;
			boolean started = false;
			System.out.println(termText + "--->Contains Ad/BC combined");
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
			} else {
				if (year < 100)
					termText = "-00" + year + "0101";
				else if (year < 1000)
					termText = "-0" + year + "0101";
				else
					termText = "-" + year + "0101";
			}
			System.out.println("Final date is : " + termText);
			token.setTermText(termText);
		} else if (termText.matches(time24HourPattern)) // time24HourPattern="([0-9]|0[0-9]|1?[0-9]|2[0-3]):[0-5]?[0-9]";
		// from 00:00 to 23:59
		{
			System.out.println("Found a time");
			Token nextToken = stream.getNext();
			String nextTerm = nextToken.toString();
			nextTerm = nextTerm.replaceAll("[^a-zA-Z]", "");
			System.out.println("modified next term is : " + nextTerm);
			if (nextTerm.equalsIgnoreCase("am")) {
				nextToken = stream.next();
				token.merge(nextToken);
				stream.remove();
				termText = termText + ":00";
				System.out.println("Final date is : " + termText);
				token.setTermText(termText);
				nextToken = stream.next();

			} else if (nextTerm.equalsIgnoreCase("pm")) {
				nextToken = stream.next();
				token.merge(nextToken);
				stream.remove();
				String[] temp = termText.split(":");
				Integer hour = Integer.parseInt(temp[0]);
				if (hour < 12)
					hour += 12;
				termText += hour + ":" + temp[1] + ":00";
				System.out.println("Final date is : " + termText);
				token.setTermText(termText);
				nextToken = stream.next();
			}
		} else if (termText.matches(hhmmPattern)) {
			// termText=termText.replaceAll("[A-Za-z]", "");

			if (termText.contains("am") || termText.contains("aM")
					|| termText.contains("Am") || termText.contains("AM")) {
				termText = termText.replaceAll("[A-Za-z]", "");
				termText = termText + ":00";
				System.out.println("Final date is : " + termText);
				token.setTermText(termText);
			} else if (termText.contains("pm") || termText.contains("pM")
					|| termText.contains("Pm") || termText.contains("PM")) {
				termText = termText.replaceAll("[A-Za-z]", "");
				System.out.println("After removal " + termText);
				String[] temp = termText.split(":");
				Integer hour = Integer.parseInt(temp[0]);
				if (hour < 12)
					hour += 12;
				termText = hour + ":" + temp[1] + ":00";
				System.out.println("Final date is : " + termText);
				token.setTermText(termText);
			}
		} else if (termText.contains("-")) {
			String[] temp = termText.split("-");
			temp[1] = temp[1].replaceAll("[^0-9]*", "");
			if (isNumber(temp[0]) && isNumber(temp[1])) {
				System.out.println(termText
						+ "--->Found year range most probably");
				if (temp[1].length() < temp[0].length()) {
					temp[1] = temp[0].substring(0, 2) + temp[1];
				}
				termText = temp[0] + "0101-" + temp[1] + "0101";
				System.out.println("Final date is " + termText);
				token.setTermText(termText);
			}
		} else {
			m = p.matcher(termText); // Matching a month for the next else-if
			if (termText.matches(time24WithSecondsHourPatternTimeZone)) // this
			// checks
			// for
			// the
			// complex
			// part
			// with
			// the
			// UTC
			{
				termText = termText.replaceAll("[A-Za-z]", "");
				System.out.println("Time with zone removed : " + termText);
				token.setTermText(termText);
				Token rest = stream.getRest();
				String restTerms = rest.toString();
				System.out.println("rest of the stream is : \n" + restTerms);
				Pattern dmy = Pattern.compile(dateMonthnameYearPattern,
						Pattern.CASE_INSENSITIVE);
				Pattern mdy = Pattern.compile(monthnameDateYearPattern,
						Pattern.CASE_INSENSITIVE);
				Pattern md = Pattern.compile(monthnameDatePattern,
						Pattern.CASE_INSENSITIVE);
				Matcher dmyMatcher = dmy.matcher(restTerms), mdyMatcher = mdy
						.matcher(restTerms), mdMatcher = md.matcher(restTerms);
				if (dmyMatcher.find()) {
					System.out.println("Found date monthname year");
					int pos = restTerms.indexOf(dmyMatcher.group());
					System.out.println("At position : " + pos);
					if (restTerms.contains(".")) {
						if (pos < restTerms.indexOf(".") && pos < 20) // Take 20
						// as
						// the
						// limit
						// of
						// number
						// of
						// characters
						// to
						// allow
						// it to
						// be
						// one
						// date.
						{
							String[] date = dmyMatcher.group().split(" ");
							Token nextToken = new Token();
							nextToken = stream.next();
							while (!nextToken.toString().equals(date[0])) {
								stream.remove();
								nextToken = stream.next();
							}
							token.merge(nextToken);
							stream.remove();
							nextToken = stream.next();
							token.merge(nextToken);
							stream.remove();
							nextToken = stream.next();
							token.merge(nextToken);
							stream.remove();
							nextToken = stream.next();
							System.out.println("Current token is : "
									+ token.toString());
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
							System.out.println("Final token is : " + finalTerm);
							token.setTermText(finalTerm);
						}
					} else if (pos < 20) {
						String[] date = dmyMatcher.group().split(" ");
						Token nextToken = new Token();
						nextToken = stream.next();
						while (!nextToken.toString().equals(date[0])) {
							stream.remove();
							nextToken = stream.next();
						}
						token.merge(nextToken);
						stream.remove();
						nextToken = stream.next();
						token.merge(nextToken);
						stream.remove();
						nextToken = stream.next();
						token.merge(nextToken);
						stream.remove();
						nextToken = stream.next();
						System.out.println("Current token is : "
								+ token.toString());
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
						System.out.println("Final token is : " + finalTerm);
						token.setTermText(finalTerm);
					}
				}

				else if (mdyMatcher.find()) {
					System.out.println("Found monthname date, year");
				} else if (mdMatcher.find()) {
					System.out.println("Found monthname date");
				}

			} else if (m.find()) {
				System.out.println(termText + "--->Found only month.");
				String month = termText;
				Token nextToken = stream.getNext();
				String nextTerm = nextToken.toString();

				if (isDate(nextTerm)) // Maybe just a month by itself followed
				// by some arbitrary words. Check if the
				// next token is a number.
				{
					Token nextToNextToken = stream.getNextToNext();
					String nextToNextTerm = new String();
					if (nextToNextToken != null) // Might be followed by year
					{
						nextToNextTerm = nextToNextToken.getTermText();
						if (isYear(nextToNextTerm)) // Format is month date year
						{
							nextToken = stream.next();
							token.merge(nextToken);
							String date = nextTerm;
							date = date.replaceAll("[,]*", "");
							System.out.println("Current Token : "
									+ token.toString());
							stream.remove();
							nextToken = stream.next();
							System.out.println("Next Token is : "
									+ nextToken.toString());

							nextTerm = nextToken.toString();
							nextTerm = nextTerm.replaceAll("[,]*", "");
							System.out.println("Modified nxt token : "
									+ nextTerm);

							String year = nextTerm;
							System.out.println(termText
									+ "--->In the format Month date, year");
							token.merge(nextToken);
							stream.remove();
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
							System.out.println("Final date is : " + finalTerm);
							token.setTermText(finalTerm);
						} else // Format is month date
						{
							nextToken = stream.next();
							token.merge(nextToken);
							String date = nextTerm;
							date = date.replaceAll("[,]*", "");
							System.out.println("Current Token : "
									+ token.toString());
							stream.remove();

							String finalTerm = "1900";
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
							System.out.println("Final date is : " + finalTerm);
							token.setTermText(finalTerm);
						}

					}
				}

			}
		}
		if (stream.hasNext()) {
			return true;
		} else
			return false;
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
		// TODO Auto-generated method stub
		term = term.replaceAll("[^0-9]*", "");
		if (term.matches("(11|12|13|14|15|16|17|18|19|20)\\d\\d"))
			return true;
		return false;
	}

	private boolean isDate(String term) {
		// TODO Auto-generated method stub
		System.out.println("Befoerr replacement : " + term);
		term = term.replaceAll("[^0-9]*", "");
		System.out.println("Replaced term : " + term);
		if (term.matches("([1-9]|0[1-9]|[1-2][0-9]|3[01])"))
			return true;
		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return stream;
	}

	private boolean isNumber(String token) {
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
