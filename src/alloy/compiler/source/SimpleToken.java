package alloy.compiler.source;

import alloy.compiler.source.Expression.*;
import java.util.Optional;

/*
Operator precedence:
dot (binary)
multiply/divide
plus/minus
and
or TODO or should have ambiguous precedence next to and
*/

public enum SimpleToken implements Token {
	OPEN_BRACE,
	CLOSE_BRACE,
	OPEN_PAREN,
	CLOSE_PAREN,
	OPEN_CHEVRON,
	CLOSE_CHEVRON,
	OPEN_BRACKET,
	CLOSE_BRACKET,
	COMMA,
	SEMICOLON,
	TAG,
	END,
	QUESTION_MARK,
	COLON,
	BITWISE_AND {
		@Override
		public Optional<BinaryOp> binaryOp() {
			return Optional.of(new BinaryOp(XOR.binaryOp().get().precedence(), Associativity.LEFT, BitwiseAndExpression::new));
		}
	},
	LOGICAL_AND {
		@Override
		public Optional<BinaryOp> binaryOp() {
			return Optional.of(new BinaryOp(XOR.binaryOp().get().precedence(), Associativity.LEFT, LogicalAndExpression::new));
		}
	},
	BITWISE_OR {
		@Override
		public Optional<BinaryOp> binaryOp() {
			return Optional.of(new BinaryOp(XOR.binaryOp().get().precedence(), Associativity.LEFT, BitwiseOrExpression::new));
		}
	},
	LOGICAL_OR {
		@Override
		public Optional<BinaryOp> binaryOp() {
			return Optional.of(new BinaryOp(XOR.binaryOp().get().precedence(), Associativity.LEFT, LogicalOrExpression::new));
		}
	},
	LOGICAL_NOT {
		@Override
		public Optional<UnaryOp> unaryOp() {
			return Optional.of(new UnaryOp(DIVIDE.binaryOp().get().precedence() + 1, LogicalNotExpression::new));
		}
	},
	BITWISE_NOT {
		@Override
		public Optional<UnaryOp> unaryOp() {
			return Optional.of(new UnaryOp(DIVIDE.binaryOp().get().precedence() + 1, BitwiseNotExpression::new));
		}
	},
	XOR {
		@Override
		public Optional<BinaryOp> binaryOp() {
			return Optional.of(new BinaryOp(EQUALS.binaryOp().get().precedence() + 1, Associativity.LEFT, ExclusiveOrExpression::new));
		}
	},
	DOT {
		@Override
		public Optional<BinaryOp> binaryOp() {
			return Optional.of(new BinaryOp(DIVIDE.binaryOp().get().precedence() + 1, Associativity.LEFT, DereferenceExpression::new));
		}
		/* TODO add as unary for dereferencing */
	},
	ADD {
		@Override
		public Optional<BinaryOp> binaryOp() {
			return Optional.of(new BinaryOp(EQUALS.binaryOp().get().precedence() + 1, Associativity.LEFT, SumExpression::new));
		}
	},
	SUBTRACT {
		@Override
		public Optional<BinaryOp> binaryOp() {
			return Optional.of(new BinaryOp(ADD.binaryOp().get().precedence(), Associativity.LEFT, DifferenceExpression::new));
		}

		@Override
		public Optional<UnaryOp> unaryOp() {
			return Optional.of(new UnaryOp(XOR.binaryOp().get().precedence() + 1, NegateExpression::new));
		}
	},
	DIVIDE {
		@Override
		public Optional<BinaryOp> binaryOp() {
			return Optional.of(new BinaryOp(ADD.binaryOp().get().precedence() + 1, Associativity.LEFT, DivideExpression::new));
		}
	},
	MULTIPLY {
		@Override
		public Optional<BinaryOp> binaryOp() {
			return Optional.of(new BinaryOp(DIVIDE.binaryOp().get().precedence() + 1, Associativity.LEFT, MultiplyExpression::new));
		}
	},
	EQUALS {
		@Override
		public Optional<BinaryOp> binaryOp() {
			return Optional.of(new BinaryOp(0, Associativity.RIGHT, AssignExpression::new));
		}
	}
}
