package alloy.compiler.source;

import java.math.BigDecimal;

public final record RationalLiteral(BigDecimal number) implements Token, Expression {

	@Override
	public Type type() {
		throw new RuntimeException("Not implemented");
	}
}
