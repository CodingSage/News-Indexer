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
		if (data.startsWith("~")) {
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
		if (isNot)
			value = "~" + value;
	}

	@Override
	public String toString() {		
		String left_str = "", right_str = "";
		if (value == null)
			return "";
		String indexStr = index == "" ? "" : index + ":";
		if (value.startsWith("~"))
			return "<" + indexStr + value.substring(1, value.length()) + ">";
		if (left != null && right != null){
			ExpressionNode l = this, r = this;
			while(l.getLeft() != null)
				l = l.getLeft();
			while(r.getRight() != null)
				r = r.getRight();
			if(!r.getIndex().equals(l.getIndex()))
				right_str = " [ " + right + " ] ";
			else
				right_str = " " + right;			
			left_str = left + " ";
		}
		return left_str + indexStr  + value + right_str;
	}

	public String getIndex() {
		return index;
	}

	public String getValue() {
		return value;
	}

	public ExpressionNode getRight() {
		return right;
	}

	public ExpressionNode getLeft() {
		return left;
	}
}
