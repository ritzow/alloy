package alloy.compiler;

import alloy.compiler.Token.NameSegment;
import alloy.compiler.Token.RationalLiteral;
import alloy.compiler.Token.SimpleToken;
import alloy.compiler.Token.TextLiteral;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlloyScanner {
	private final BufferedReader in;
	private final List<Integer> buffer;
	private int codePoint;
	private int lineNumber;

	public AlloyScanner(Path file) throws IOException {
		this.in = Files.newBufferedReader(file, StandardCharsets.UTF_8);
		this.buffer = new ArrayList<>(32);
		this.codePoint = nextCodePoint();
		this.lineNumber = 1;
	}

	public Token next() throws IOException {
		Token token;
		do {
			token = parseNext();
		} while(token == null);
		return token;
	}

	private Token parseNext() throws IOException {
		return switch(codePoint) {
			case -1 -> SimpleToken.END;
			case '\t', ' ', '\r' -> advance(null);
			case '\n' -> {
				lineNumber++;
				yield advance(null);
			}
			case '#' -> advance(SimpleToken.TAG);
			case '{' -> advance(SimpleToken.OPEN_SUB);
			case '}' -> advance(SimpleToken.CLOSE_SUB);
			case '(' -> advance(SimpleToken.OPEN_PAREN);
			case ')' -> advance(SimpleToken.CLOSE_PAREN);
			case ',' -> advance(SimpleToken.COMMA);
			case ';' -> advance(SimpleToken.SEMICOLON);
			case '.' -> advance(SimpleToken.DOT);
			case '"' -> {
				while((codePoint = nextCodePoint()) != '"') {
					buffer.add(codePoint);
				}
				yield new TextLiteral(bufferToString(buffer));
			}
			default -> {
				if(Character.isAlphabetic(codePoint)) {
					buffer.add(codePoint);
					while(Character.isLetterOrDigit(codePoint = nextCodePoint())) {
						buffer.add(codePoint);
					}

					yield new NameSegment(bufferToString(buffer));
				} else if(Character.isDigit(codePoint)) {
					while(Character.isDigit(codePoint = nextCodePoint())) {
						buffer.add(codePoint);
					}

					if (codePoint == '.') {
						buffer.add(codePoint);

						codePoint = nextCodePoint();

						if(Character.isDigit(codePoint)) {
							while(Character.isDigit(codePoint = nextCodePoint())) {
								buffer.add(codePoint);
							}
						} else {
							throw new TokenException();
						}
					}
					yield new RationalLiteral(new BigDecimal(bufferToString(buffer)));
				} else {
					throw new TokenException("Unknown start of token: " + Character.getName(codePoint) + " on line " + lineNumber);
				}
			}
		};
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
				return unit0;
			}
		} else {
			in.close();
			return -1;
		}
	}
}
