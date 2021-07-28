package alloy.compiler.source;

import alloy.compiler.SourceReconstructable;
import alloy.compiler.source.Expression.*;
import java.util.List;

public sealed interface Expression extends SourceReconstructable permits
	//Expression.CallExpression,
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
	AssignExpression,
	ExclusiveOrExpression,
	BitwiseNotExpression,
	LogicalNotExpression,
	LogicalOrExpression,
	BitwiseOrExpression,
	LogicalAndExpression,
	BitwiseAndExpression {

	default Type type() {
		throw new UnsupportedOperationException("Not implemented");
	}

	/* These will resolve to interface calls during type checking */
	final record SumExpression(Expression left, Expression right) implements Expression {
		@Override
		public String toSource() {
			return "(" + left.toSource() + " + " + right.toSource() + ")";
		}
	}
	final record DifferenceExpression(Expression left, Expression right) implements Expression {
		@Override
		public String toSource() {
			return "(" + left.toSource() + " - " + right.toSource() + ")";
		}
	}
	final record MultiplyExpression(Expression left, Expression right) implements Expression {
		@Override
		public String toSource() {
			return "(" + left.toSource() + " * " + right.toSource() + ")";
		}
	}
	final record DivideExpression(Expression left, Expression right) implements Expression {
		@Override
		public String toSource() {
			return "(" + left.toSource() + " / " + right.toSource() + ")";
		}
	}
	final record DereferenceExpression(Expression left, Expression right) implements Expression {
		@Override
		public String toSource() {
			return "(" + left.toSource() + "." + right.toSource() + ")";
		}
	}
	final record AssignExpression(Expression left, Expression right) implements Expression {
		@Override
		public String toSource() {
			return "(" + left.toSource() + " = " + right.toSource() + ")";
		}
	}
	final record NegateExpression(Expression expr) implements Expression {
		@Override
		public String toSource() {
			return "-(" + expr.toSource() + ")";
		}
	}
	final record ExclusiveOrExpression(Expression left, Expression right) implements Expression {
		@Override
		public String toSource() {
			return "(" + left.toSource() + " ^ " + right.toSource() + ")";
		}
	}
	final record BitwiseNotExpression(Expression expr) implements Expression {
		@Override
		public String toSource() {
			return "(~" + expr.toSource() + ")";
		}
	}
	final record LogicalNotExpression(Expression expr) implements Expression {
		@Override
		public String toSource() {
			return "!(" + expr.toSource() + ")";
		}
	}
	final record LogicalOrExpression(Expression left, Expression right) implements Expression {
		@Override
		public String toSource() {
			return "(" + left.toSource() + " || " + right.toSource() + ")";
		}
	}
	final record BitwiseOrExpression(Expression left, Expression right) implements Expression {
		@Override
		public String toSource() {
			return "(" + left.toSource() + " | " + right.toSource() + ")";
		}
	}
	final record LogicalAndExpression(Expression left, Expression right) implements Expression {
		@Override
		public String toSource() {
			return "(" + left.toSource() + " && " + right.toSource() + ")";
		}
	}
	final record BitwiseAndExpression(Expression left, Expression right) implements Expression {
		@Override
		public String toSource() {
			return "(" + left.toSource() + " & " + right.toSource() + ")";
		}
	}

	/*final class CallExpression implements Expression {
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
	}*/
}
