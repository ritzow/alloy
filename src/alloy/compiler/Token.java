package alloy.compiler;

import java.math.BigDecimal;

public sealed interface Token {
	enum SimpleToken implements Token {
		OPEN_SUB,
		CLOSE_SUB,
		OPEN_PAREN,
		CLOSE_PAREN,
		COMMA,
		SEMICOLON,
		TAG,
		DOT,
		END,
		ADD,
		SUBTRACT,
		DIVIDE,
		MULTIPLY
	}

	final record NameSegment(String text) implements Token {
	}

	final record TextLiteral(String text) implements Token {

	}

	final class CharacterLiteral implements Token {

	}

	final record RationalLiteral(BigDecimal number) implements Token {

	}

}
