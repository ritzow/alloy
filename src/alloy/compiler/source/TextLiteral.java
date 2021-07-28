package alloy.compiler.source;

public final record TextLiteral(String text) implements Token, Expression {

	@Override
	public Type type() {
		/* TODO return string literal type */
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String toSource() {
		return "\"" + text + "\"";
	}
}
