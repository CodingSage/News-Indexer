package edu.buffalo.cse.irf14.query;

/**
 * Class that represents a parsed query
 * 
 * @author nikhillo
 *
 */
public class Query {

	public static final String[] OPERATORS = { "AND", "OR" };
	private ExpressionNode query;
	private String defaultOperator;

	public Query(String query, String defaultOperator) {
		this.defaultOperator = defaultOperator;
		this.query = parseQuery(query);
	}

	private ExpressionNode parseQuery(String query) {
		query = query.trim();
		if (query.startsWith("(") && query.endsWith(")"))
			query = query.substring(1, query.length() - 1);
		String[] parts = query.split(" ");
		if (parts.length < 2)
			return new ExpressionNode(parts[0]);
		if (parts.length == 2) {
			if (parts[0].toLowerCase().equals("not"))
				return new ExpressionNode("~" + parts[1]);
			return new ExpressionNode(defaultOperator, new ExpressionNode(
					parts[0]), new ExpressionNode(parts[1]));
		}
		int j = 0, index = 0;
		while (!isOperator(parts[j]) && !parts[j].startsWith("(")) {
			index += parts[j].length() + 1;
			j++;
		}
		String left = query.substring(0, index - 1);
		String right = query.substring(index + parts[j].length(), query.length());
		return new ExpressionNode(parts[j], parseQuery(left), parseQuery(right));
	}

	private boolean isOperator(String str) {
		for (String operator : OPERATORS) {
			if (operator.toLowerCase().equals(str.toLowerCase()))
				return true;
		}
		return false;
	}

	/**
	 * Method to convert given parsed query into string
	 */
	public String toString() {
		StringBuilder str;
		if (query.toString().startsWith("[")) {
			str = new StringBuilder(query.toString());
			str.replace(0, 1, "{");
			str.replace(str.length() - 1, str.length(), "}");
		}else{
			str = new StringBuilder("{" + query + "}");
		}
		return str.toString();
	}
}
