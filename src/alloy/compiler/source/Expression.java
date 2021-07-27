package alloy.compiler.source;

import alloy.compiler.source.Expression.*;
import java.util.List;

public sealed interface Expression permits
	Expression.CallExpression,
	Expression.SumExpression,
	Name,
	CharacterLiteral,
	NameSegment,
	RationalLiteral,
	TextLiteral,
	DifferenceExpression,
	MultiplyExpression,
	DivideExpression,
	NegateExpression,
	DereferenceExpression,
	AssignExpression {

	default Type type() {
		throw new UnsupportedOperationException("Not implemented");
	}

	final record SumExpression(Expression left, Expression right) implements Expression {}
	final record DifferenceExpression(Expression left, Expression right) implements Expression {}
	final record MultiplyExpression(Expression left, Expression right) implements Expression {}
	final record DivideExpression(Expression left, Expression right) implements Expression {}
	final record DereferenceExpression(Expression left, Expression right) implements Expression {}
	final record AssignExpression(Expression left, Expression right) implements Expression {}
	final record NegateExpression(Expression expr) implements Expression {}

	final class CallExpression implements Expression {
		Expression subject;
		List<Expression> parameters;

		public CallExpression(Expression subject, Expression... parameters) {
			this.subject = subject;
			this.parameters = List.of(parameters);
		}

		public CallExpression(Expression subject, List<Expression> parameters) {
			this.subject = subject;
			this.parameters = parameters;
		}

		@Override
		public String toString() {
			return "CallExpression{" +
				"subject=" + subject +
				", parameters=" + parameters +
				'}';
		}

		@Override
		public Type type() {
			throw new UnsupportedOperationException("Not implemented");
		}
	}
}
