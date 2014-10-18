package edu.buffalo.cse.irf14.query;

/**
 * Class that represents a parsed query
 * 
 * @author nikhillo
 *
 */
public class Query {

	private ExpressionNode query;
	private String normalizedQuery;

	public Query(String normalizedquery, ExpressionNode queryNode) {
		this.normalizedQuery = normalizedquery;
		this.query = queryNode;
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
		} else {
			str = new StringBuilder("{ " + query + " }");
		}
		return str.toString();
	}
}
