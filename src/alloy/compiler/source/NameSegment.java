package alloy.compiler.source;

import alloy.compiler.model.Expression;
import alloy.compiler.model.Type;

public final record NameSegment(String text) implements Token, Expression {
	@Override
	public Type type() {
		throw new UnsupportedOperationException("Second pass type reference lookup not implemented");
	}
}
