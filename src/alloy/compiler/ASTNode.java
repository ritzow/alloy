package alloy.compiler;

public class ASTNode {
	private int lineNumber, columnNumber;

	public ASTNode(int lineNumber, int columnNumber) {
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}
}
