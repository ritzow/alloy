package alloy.compiler.source;

import alloy.compiler.Environment;
import alloy.compiler.source.AlloyScanner.TokenResult;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static alloy.compiler.source.SimpleToken.DOT;

/** Recursive descent parser for the Alloy programming language
 * Parses a single finite length character sequence **/
public class AlloyParser {
	private static final int AVERAGE_NAME_LENGTH = 4;

	private final AlloyScanner scan;
	private final Environment env;
	private TokenResult current;

	public static List<Block> parse(Reader reader, Environment env) {
		try {
			return new AlloyParser(reader, env).parse();
		} catch(IOException | RuntimeException e) {
			env.log(e);
			return List.of();
		}
	}

	private AlloyParser(Reader reader, Environment env) throws IOException {
		this.scan = new AlloyScanner(reader);
		this.env = env;
		this.current = scan.next();
	}

	/** Parse this file, updating the AlloyEnvironment
	and return references to the modules it contained **/
	public List<Block> parse() throws IOException {
		List<Block> blocks = matchBlocks();
		match(SimpleToken.END);
		return blocks;
	}

	/** A series of tags and expressions followed by a block or semicolon **/
	private List<Block> matchBlocks() throws IOException {
		List<Block> blocks = new ArrayList<>();
		Optional<Block> b;
		while((b = matchBlock()).isPresent()) {
			blocks.add(b.get());
		}
		return blocks;
	}

	private Optional<Block> matchBlock() throws IOException {
		List<Tag> tags = new ArrayList<>();
		do {
			/* Match tags up until semi or sub */
			if(peek() instanceof SimpleToken tok) {
				switch(tok) {
					case TAG -> tags.add(matchTag());
					case OPEN_BRACE -> {
						match();
						List<Block> sub = matchBlocks();
						match(SimpleToken.CLOSE_BRACE);
						return Optional.of(new Block(tags, sub));
					}
					case SEMICOLON -> {
						match();
						return Optional.of(new Block(tags, List.of()));
					}
					default -> {
						if(!tags.isEmpty()) {
							throw new ASTException("Block is not closed " + current);
						}
						return Optional.empty();
					}
				}
			} else {
				if(!tags.isEmpty()) {
					throw new ASTException("Block is not closed " + current);
				}
				return Optional.empty();
			}
		} while(true);
	}

	/** Match a #, name, followed by any number of expressions **/
	private Tag matchTag() throws IOException {
		match(SimpleToken.TAG);
		/* Lookup tag name to find tag handler */
		return new Tag(matchName(), matchExpressions());
	}

	private List<Expression> matchExpressions() throws IOException {
		List<Expression> elist = new ArrayList<>(1);

		/* Match a list of expressions to pass to the tag */
		do {
			Optional<Expression> e = matchExpression();
			if(e.isPresent()) {
				elist.add(e.get());
			} else {
				break;
			}
		} while(true);
		return elist;
	}

	private Optional<Expression> matchExpression() throws IOException {
		return matchExpression(0);
	}

	/* Precedence climbing algorithm from https://www.engr.mun.ca/~theo/Misc/exp_parsing.htm */

	private Optional<Expression> matchExpression(int precedence) throws IOException {
		 Optional<Expression> a = matchLeftmostExpression();
		 if(a.isEmpty()) {
		 	return a;
		 }
		 Expression t = a.get();
		 while(peek().binaryOp().isPresent() && peek().binaryOp().get().precedence() >= precedence) {
		 	Token op = peek();
		 	TokenResult pos = current;
		 	int prec = switch(peek().binaryOp().get().assoc()) {
		 		case RIGHT -> peek().binaryOp().get().precedence();
		 		case LEFT -> peek().binaryOp().get().precedence() + 1;
		    };
		 	match();
		 	Optional<Expression> e = matchExpression(prec);
		 	if(e.isEmpty()) {
		 		throw new ASTException("Missing right hand side of operand " + pos);
		    }
		 	t = op.binaryOp().get().create().apply(t, e.get());
		 }
		 return Optional.of(t);
	}

	private Optional<Expression> matchLeftmostExpression() throws IOException {
		if(peek().unaryOp().isPresent()) {
			Token op = peek();
			TokenResult pos = current;
			match();
			var t = matchExpression(op.unaryOp().get().precedence());
			if(t.isPresent()) {
				return Optional.of(op.unaryOp().get().create().apply(t.get()));
			} else {
				throw new ASTException("Missing right hand side of operand " + pos);
			}
		} else if(peek() == SimpleToken.OPEN_PAREN) {
			match();
			var t = matchExpression(0);
			match(SimpleToken.CLOSE_PAREN);
			return t;
		} else if(peek() instanceof Expression e) {
			match();
			return Optional.of(e);
		} else {
			return Optional.empty();
		}
	}

	/** Match a name: character strings separated by dots, only useful for tag names **/
	private Name matchName() throws IOException {
		List<NameSegment> segments = new ArrayList<>(AVERAGE_NAME_LENGTH);
		do {
			/* TODO needs to reuse name elements if it does not match a name */
			/* Or: What to do if token after dot is not name? */
			if(peek() instanceof NameSegment seg) {
				match();
				segments.add(seg);
				if(peek() != DOT) {
					return Name.ofSegments(segments);
				} else {
					match(DOT);
				}
			} else {
				throw new ASTException("Name expected");
			}
		} while(true);
	}

	private void match() throws IOException {
		if(peek() == SimpleToken.END) {
			throw new ASTException("Went past end of file");
		}
		current = scan.next();
	}

	private Token peek() {
		return current.token();
	}

	private void match(SimpleToken token) throws IOException {
		if(peek() != token) {
			throw new ASTException(current);
		}
		current = scan.next();
	}
}
