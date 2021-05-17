package alloy.compiler.symbol;

public enum StandardTokens {
	TAG('#');

	private final int codePoint;

	StandardTokens(int codePoint) {
		this.codePoint = codePoint;
	}
}
