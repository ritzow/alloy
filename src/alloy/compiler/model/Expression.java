package alloy.compiler.model;

public sealed interface Expression permits
	alloy.compiler.Token.CharacterLiteral,
	alloy.compiler.Token.RationalLiteral,
	alloy.compiler.Token.TextLiteral,
	Expression.BinaryInfix,
	Name {

	Type type();

	final class BinaryInfix implements Expression {
		Expression left, right;
		Operator op;

		@Override
		public Type type() {
			return left.type();
		}

		public enum Operator {
			SUM,
			DIFFERENCE,
			PRODUCT,
			QUOTIENT,
			REMAINDER
		}
	}
}
