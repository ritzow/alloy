package alloy.compiler.source;

import java.util.Optional;

public sealed interface Token permits CharacterLiteral, NameSegment, RationalLiteral, SimpleToken, TextLiteral {

	default Optional<BinaryOp> binaryOp() {
		return Optional.empty();
	}

	default Optional<UnaryOp> unaryOp() {
		return Optional.empty();
	}

	enum Associativity {
		LEFT,
		RIGHT
	}
}
