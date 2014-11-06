/**
 * 
 */
package edu.buffalo.cse.irf14.query;

/**
 * @author nikhillo Static parser that converts raw text to Query objects
 */
public class QueryParser {

	public static final String DEFAULT_INDEX = "Term";
	public static final String[] OPERATORS = { "AND", "OR" };
	private static String default_Operator;

	/**
	 * MEthod to parse the given user query into a Query object
	 * 
	 * @param userQuery
	 *            : The query to parse
	 * @param defaultOperator
	 *            : The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator) {
		default_Operator = defaultOperator;
		String normalizedQuery = normalizeQuery(userQuery, "");
		return new Query(normalizedQuery, evaluateQuery(normalizedQuery));
	}

	// TODO Refactor: clean this, repetition of cases
	private static ExpressionNode evaluateQuery(String query) {
		query = query.trim();
		//reference: http://stackoverflow.com/questions/21627866/java-regex-to-split-a-string-using-spaces-but-not-considering-double-quotes-or
		String[] parts = query.split(" (?=(([^'\"]*['\"]){2})*[^'\"]*$)");
		int j = 0, index = 0;
		if (query.startsWith("(")) {
			while (!parts[j].endsWith(")")) {
				index += parts[j].length() + 1;
				j++;
			}
			index += parts[j].length() + 1;
			if (j == parts.length - 1) {
				query = query.substring(1, query.length() - 1);
				parts[0] = parts[0].substring(1);
				parts[j] = parts[j].substring(0, parts[j].length() - 1);
			} else if (isOperator(parts[j + 1])) {
				String left = query.substring(0, index - 1);
				String right = query.substring(index + parts[j+1].length(), query.length());
				return new ExpressionNode(parts[j + 1], evaluateQuery(left), evaluateQuery(right));
			} else {
				if (parts[0].toLowerCase().equals("not")) {
					index += parts[j].length() + 1;
					j++;
				}
				String op = parts[j+1].toLowerCase().equals("not") ? "AND" : default_Operator;
				return new ExpressionNode(op, evaluateQuery(query.substring(0, index - 1)), 
						evaluateQuery(query.substring(index, query.length())));
			}
		}
		if (parts.length < 2)
			return new ExpressionNode(parts[0]);
		if (parts.length == 2) {
			if (parts[0].toLowerCase().equals("not"))
				return new ExpressionNode("~" + parts[1]);
			return new ExpressionNode(default_Operator, new ExpressionNode(parts[0]), new ExpressionNode(parts[1]));
		}
		if(parts.length == 3 && isOperator(parts[1]))
			return new ExpressionNode(parts[1], evaluateQuery(parts[0]), evaluateQuery(parts[2]));
		while (j < parts.length && !isOperator(parts[j]) && !parts[j].startsWith("(")) {
			index += parts[j].length() + 1;
			j++;
		}
		if (j == parts.length) {
			j = index = 0;
			index += parts[j].length() + 1;
			j++;			
			if (parts[j - 1].toLowerCase().equals("not")) {
				index += parts[j].length() + 1;
				j++;
			}
			String op = parts[j].toLowerCase().equals("not") ? "AND" : default_Operator;
			String left = query.substring(0, index - 1);
			String right = query.substring(index, query.length());
			return new ExpressionNode(op, evaluateQuery(left), evaluateQuery(right));
		}
		if (index == 0 && j == 0) {
			if (parts[j].startsWith("(")) {
				while (!parts[j].endsWith(")")) {
					index += parts[j].length() + 1;
					j++;
				}
			}
			index += parts[j].length() + 1;
			j++;
		}
		String left = query.substring(0, index - 1);
		String right = query.substring(index + parts[j].length(), query.length());
		return new ExpressionNode(parts[j], evaluateQuery(left), evaluateQuery(right));
	}

	public static String normalizeQuery(String userQuery, String indexName) {
		int countf = 0, countb = 0, index = 0;
		String query = "";
		String[] str = userQuery.split(" ");
		for (String s : str) {
			index++;
			if (s == "")
				continue;
			while (s.startsWith("(")) {
				countf++;
				s = s.substring(1);
			}
			while (s.endsWith(")")) {
				countb++;
				s = s.substring(0, s.length() - 1);
			}
			if (s.startsWith("\"") && !s.equals("")) {
				int j = index;
				while (!str[j].endsWith("\"")) {
					if(str[j].equals("")){
						j++;
						continue;
					}						
					s += " " + str[j];
					str[j] = "";
					j++;
				}
				s += " " + str[j];
				str[j] = "";
			}
			if (!s.contains(":") && !isOperator(s) && !s.toLowerCase().equals("not") && !s.equals("")) {
				String i = indexName == "" ? DEFAULT_INDEX : indexName;
				s = i + ":" + s;
			}
			if (s.contains(":(") && !s.equals("")) {
				int j = index + 1;
				int indexPartition = s.indexOf(':');
				String sindex = s.substring(0, indexPartition);
				String sterms = s.substring(indexPartition + 1);
				while (j < str.length && !str[j].endsWith(")")) {
					str[j] = normalizeQuery(str[j], sindex);
					j++;
				}
				if (j < str.length) {
					str[j] = normalizeQuery(str[j], sindex);
					s = normalizeQuery(sterms, sindex);
				}
			}
			while (countf != 0) {
				countf--;
				s = "(" + s.trim();
			}
			while (countb != 0) {
				countb--;
				s = s.trim();
				s += ")";
			}
			if (query != "" && !s.equals("") && !query.endsWith("(") && !s.equals(")"))
				query += " ";
			query += s;
		}
		return query;
	}

	public static boolean isOperator(String str) {
		for (String operator : OPERATORS) {
			if (operator.toLowerCase().equals(str.toLowerCase()))
				return true;
		}
		return false;
	}
}
