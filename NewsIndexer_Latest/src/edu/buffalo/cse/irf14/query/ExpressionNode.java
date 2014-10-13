package edu.buffalo.cse.irf14.query;

public class ExpressionNode {

	String value;
	ExpressionNode right;
	ExpressionNode left;

	public ExpressionNode(String value) {
		this.value = value;
	}

	public ExpressionNode(String value, ExpressionNode left,
			ExpressionNode right) {
		this.value = value;
		this.left = left;
		this.right = right;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ExpressionNode getRight() {
		return right;
	}

	public void setRight(ExpressionNode right) {
		this.right = right;
	}

	public ExpressionNode getLeft() {
		return left;
	}

	public void setLeft(ExpressionNode left) {
		this.left = left;
	}

	@Override
	public String toString() {
		if (value == null)
			return "";
		if (value.startsWith("~"))
			return "<" + value.substring(1, value.length()) + ">";
		String str = value;
		if (left != null && right != null)
			str = "[" + left + " " + str + " " + right + "]";
		return str;
	}
}
