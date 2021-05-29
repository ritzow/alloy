package alloy.compiler.model;

import alloy.compiler.source.Token;

public sealed interface Expression permits
	Token.CharacterLiteral,
	Token.RationalLiteral,
	Token.TextLiteral,
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
