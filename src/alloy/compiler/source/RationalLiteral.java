package alloy.compiler.source;

import alloy.compiler.model.Expression;
import alloy.compiler.model.Type;
import java.math.BigDecimal;

public final record RationalLiteral(BigDecimal number) implements Token, Expression {

	@Override
	public Type type() {
		throw new RuntimeException("Not implemented");
	}
}
