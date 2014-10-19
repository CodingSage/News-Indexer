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

	public ExpressionNode getQuery() {
		return query;
	}

	/**
	 * Method to convert given parsed query into string
	 */
	public String toString() {
		String str = query.toString();
		return "{ " + str + " }";
	}
}
