package alloy.compiler;

import alloy.compiler.model.Expression;
import alloy.compiler.model.Type;
import java.math.BigDecimal;

public sealed interface Token {

	enum SimpleToken implements Token {
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

	final record TextLiteral(String text) implements Token, Expression {

		@Override
		public Type type() {
			/* TODO return string literal type */
			throw new RuntimeException("Not implemented");
		}
	}

	final record CharacterLiteral(int codePoint) implements Token, Expression {
		@Override
		public String toString() {
			return "CharacterLiteral[" +
				"codePoint=" + (Character.isSpaceChar(codePoint)
				? ("0x" + Integer.toHexString(codePoint)) : (
				'\'' + Character.toString(codePoint)) + '\'') +
				" (" + Character.getName(codePoint) + ")]";
		}

		@Override
		public Type type() {
			throw new RuntimeException("Not implemented");
		}
	}

	final record RationalLiteral(BigDecimal number) implements Token, Expression {

		@Override
		public Type type() {
			throw new RuntimeException("Not implemented");
		}
	}

}
