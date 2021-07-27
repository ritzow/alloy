package alloy.compiler.source;

import alloy.compiler.model.Expression;
import alloy.compiler.model.Type;

public final record TextLiteral(String text) implements Token, Expression {

	@Override
	public Type type() {
		/* TODO return string literal type */
		throw new RuntimeException("Not implemented");
	}
}
