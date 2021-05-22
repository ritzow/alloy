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

	final record NameSegment(String text) implements Token, Nameable {
	}

	final record TextLiteral(String text) implements Token {

	}

	final record CharacterLiteral(int codePoint) implements Token {
		@Override
		public String toString() {
			return "CharacterLiteral[" +
				"codePoint=" + (Character.isSpaceChar(codePoint)
				? ("0x" + Integer.toHexString(codePoint)) : (
				'\'' + Character.toString(codePoint)) + '\'') +
				" (" + Character.getName(codePoint) + ")]";
		}
	}

	final record RationalLiteral(BigDecimal number) implements Token {

	}

}
