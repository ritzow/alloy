package alloy.compiler.source;

public final record NameSegment(String text) implements Token, Expression {
	@Override
	public Type type() {
		throw new UnsupportedOperationException("Second pass type reference lookup not implemented");
	}
}
