package edu.buffalo.cse.irf14.query;

public class ExpressionNode {

	String index;
	String value;
	ExpressionNode right;
	ExpressionNode left;

	public ExpressionNode(String data) {
		setVariables(data);
	}

	public ExpressionNode(String data, ExpressionNode left, ExpressionNode right) {
		setVariables(data);
		this.left = left;
		this.right = right;
	}

	private void setVariables(String data) {
		boolean isNot = false;
		if(data.startsWith("~")){
			isNot = true;
			data = data.substring(1);
		}			
		String[] s = data.split(":");
		if (s.length > 1) {
			value = s[1];
			index = s[0];
		} else {
			value = s[0];
			index = QueryParser.isOperator(value) ? "" : QueryParser.DEFAULT_INDEX;
		}
		if(isNot)
			value = "~" + value;
	}

	@Override
	public String toString() {		
		String indexStr = index == "" ? "" : index + ":";
		if (value == null)
			return "";
		if (value.startsWith("~"))
			return "<" + indexStr + value.substring(1, value.length()) + ">";		
		String str = indexStr  + value;
		if (left != null && right != null)
			str = "[ " + left + " " + str + " " + right + " ]";
		return str;
	}
}
