package alloy.compiler.source;

import alloy.compiler.model.BinaryOp;
import alloy.compiler.model.Expression;
import alloy.compiler.model.Expression.*;
import alloy.compiler.model.Type;
import alloy.compiler.model.UnaryOp;
import java.math.BigDecimal;
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
