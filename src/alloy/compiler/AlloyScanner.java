package alloy.compiler;

import alloy.compiler.Token.*;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static alloy.compiler.Token.SimpleToken.*;

public class AlloyScanner {
	private final Reader in;
	private final List<Integer> buffer;
	private int codePoint;
	private int line, column;

	public AlloyScanner(Reader in) throws IOException {
		this.in = in;
		this.buffer = new ArrayList<>(32);
		this.codePoint = nextCodePoint();
		this.line = 1;
	}

	public static record ScanContext(int lineNumber, int columnNumber) {

	}

	public static record TokenResult(Token token, ScanContext context) {
		@Override
		public String toString() {
			return "(" + context.lineNumber + "," + context.columnNumber + ") "  + token;
		}
	}

	public TokenResult next() throws IOException {
		Token token;
		int linestart, columnstart;
		do
		{
			linestart = line;
			columnstart = column;
			token = parseNext();
		} while(token == null);
		return new TokenResult(token, new ScanContext(linestart, columnstart));
	}

	private Token parseNext() throws IOException {
		return switch(codePoint) {
			case -1 -> END;
			case '\t', ' ', '\r' -> advance(null);
			case '\n' -> {
				newline();
				yield advance(null);
			}
			case '#' -> advance(TAG);
			case '{' -> advance(OPEN_BRACE);
			case '}' -> advance(CLOSE_BRACE);
			case '(' -> advance(OPEN_PAREN);
			case ')' -> advance(CLOSE_PAREN);
			case '[' -> advance(OPEN_BRACKET);
			case ']' -> advance(CLOSE_BRACKET);
			case ',' -> advance(COMMA);
			case ';' -> advance(SEMICOLON);
			case '.' -> advance(DOT);
			case '<' -> advance(OPEN_CHEVRON);
			case '>' -> advance(CLOSE_CHEVRON);
			case '-' -> advance(MINUS);
			case '+' -> advance(ADD);
			case '*' -> advance(MULTIPLY);
			case '=' -> advance(EQUALS);
			case '?' -> advance(QUESTION_MARK);
			case ':' -> advance(COLON);
			/* UnicodeCodePointLiteral */
			case '\'' -> {
				if((codePoint = nextCodePoint()) == '\'') {
					throw new TokenException("Empty character literal");
				} else {
					int txt = codePoint;

					if((codePoint = nextCodePoint()) != '\'') {
						throw new TokenException("Too many characters in character literal: "
							+ Character.getName(codePoint));
					}

					codePoint = nextCodePoint();
					yield new CharacterLiteral(txt);
				}
			}

			/* Comments */
			case '/' -> switch(codePoint = nextCodePoint()) {
				case '/' -> {
					/* double slash "//" go to end of line. */
					do {
						switch(codePoint = nextCodePoint()) {
							case '\n' -> {
								newline();
								yield advance(null);
							}
							case -1 -> {
								yield END;
							}
							default -> {/* keep reading */}
						}
					} while(true);
				}
				case '*' -> {
					yield skipMultilineComment();
				}
				default -> DIVIDE;
			};

			/* UnicodeStringLiteral */
			case '"' -> {
				/* TODO set line on newline */
				while((codePoint = nextCodePoint()) != '"' && codePoint != -1) {
					buffer.add(codePoint);
					if(codePoint == '\n') {
						newline();
					}
				}

				if(codePoint == -1) {
					throw new TokenException("Unclosed string");
				}

				codePoint = nextCodePoint();

				yield new TextLiteral(bufferToString(buffer));
			}

			default -> {
				/* NameSegment */
				if(Character.isAlphabetic(codePoint) || codePoint == '_') {
					buffer.add(codePoint);
					while(Character.isLetterOrDigit(codePoint = nextCodePoint()) || codePoint == '_') {
						buffer.add(codePoint);
					}

					yield new NameSegment(bufferToString(buffer));
				}

				/* RationalLiteral */
				else if(Character.isDigit(codePoint)) {
					buffer.add(codePoint);
					while(Character.isDigit(codePoint = nextCodePoint())) {
						buffer.add(codePoint);
					}

					if(codePoint == '.') {
						buffer.add(codePoint);

						codePoint = nextCodePoint();

						if(Character.isDigit(codePoint)) {
							do {
								buffer.add(codePoint);
								codePoint = nextCodePoint();
							} while(Character.isDigit(codePoint));
						} else {
							throw new TokenException("RationalLiteral can't end in dot");
						}
					}
					yield new RationalLiteral(new BigDecimal(bufferToString(buffer)));
				} else {
					throw new TokenException("Unknown start of token: "
						+ Character.getName(codePoint) + " on line " + line);
				}
			}
		};
	}

	private void newline() {
		line++;
		column = 0;
	}

	//TODO count line numbers
	private Token skipMultilineComment() throws IOException {
		int depth = 1;
		codePoint = nextCodePoint();
		do {
			switch(codePoint) {
				case '/' -> {
					if((codePoint = nextCodePoint()) == '*') {
						depth++;
					} else if(codePoint == -1) {
						throw new
							TokenException("Early end of file during " +
							"multiline comment parse");
					} //else keep going with this one
				}
				case '*' -> {
					if((codePoint = nextCodePoint()) == '/') {
						depth--;
						if(depth == 0) {
							/* Successfully found end of outermost comment */
							return advance(null);
						}
					} else if(codePoint == -1) {
						throw new
							TokenException("Early end of file during " +
							"multiline comment parse");
					} //else keep going
				}
				case -1 -> throw new
					TokenException("Early end of file during " +
					"multiline comment parse");
				case '\n' -> {
					newline();
					advance();
				}
				default -> advance();
			}
		} while(true);
	}

	private void advance() throws IOException {
		codePoint = nextCodePoint();
	}

	private Token advance(Token token) throws IOException {
		codePoint = nextCodePoint();
		return token;
	}

	private static String bufferToString(List<Integer> buffer) {
		int[] codePoints = buffer.stream().mapToInt(Integer::intValue).toArray();
		buffer.clear();
		return new String(codePoints, 0, codePoints.length);
	}

	private int nextCodePoint() throws IOException {
		int unit0 = in.read();
		if(unit0 >= 0) {
			if(Character.isHighSurrogate((char)unit0)) {
				int unit1 = in.read();
				if(unit1 >= 0) {
					if(Character.isLowSurrogate((char)unit1)) {
						column++;
						return Character.toCodePoint((char)unit0, (char)unit1);
					} else {
						in.close();
						throw new RuntimeException("Invalid surrogate pair");
					}
				} else {
					/* TODO what should happen here? */
					in.close();
					return -1;
				}
			} else {
				column++;
				return unit0;
			}
		} else {
			in.close();
			return -1;
		}
	}
}
