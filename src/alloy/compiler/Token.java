package alloy.compiler;

import java.math.BigDecimal;

public sealed interface Token {
	enum SimpleToken implements Token {
		OPEN_SUB,
		CLOSE_SUB,
		OPEN_PAREN,
		CLOSE_PAREN,
		OPEN_TYPE_PARAM,
		CLOSE_TYPE_PARAM,
		COMMA,
		SEMICOLON,
		TAG,
		DOT,
		END,
		ADD,
		MINUS,
		DIVIDE,
		MULTIPLY,
		EQUALS,
		QUESTION_MARK,
		COLON
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
