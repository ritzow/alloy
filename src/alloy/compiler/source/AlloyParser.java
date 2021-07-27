package alloy.compiler.source;

import alloy.compiler.Environment;
import alloy.compiler.model.Expression.CallExpression;
import alloy.compiler.source.AlloyScanner.TokenResult;
import alloy.compiler.model.*;
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

		/* Read in any remaining tokens and print them out */
		if(peek() != SimpleToken.END) {
			System.out.println("Unparsed tokens:");
		}
		while(peek() != SimpleToken.END) {
			System.out.println(current = scan.next());
		}

		for(Block block : blocks) {
			System.out.println(block);
		}

		return blocks;
	}

	/** A series of tags and expressions followed by a block or semicolon **/
	private List<Block> matchBlocks() throws IOException {
		List<Block> blocks = new ArrayList<>();
		Block b;
		while((b = matchBlock()) != null) {
			blocks.add(b);
		}
		return blocks;
	}

	private Block matchBlock() throws IOException {
		List<Tag> tags = new ArrayList<>();
		do {
			/* Match tags up until semi or sub */
			if(peek() instanceof SimpleToken tok) {
				switch(tok) {
					case TAG -> tags.add(matchTag());
					case OPEN_BRACE -> {
						match();
						env.log("Sub start");
						List<Block> sub = matchBlocks();
						match(SimpleToken.CLOSE_BRACE);
						env.log("Sub end");
						/* TODO return with sub */
						return new Block(tags, sub);
					}
					case SEMICOLON -> {
						match();
						return new Block(tags, List.of());
					}
					default -> {
						/* TODO try to match expressions */
						/* Allow matchBlocks to handle */
						return null;
					}
				}
			} else
				return null;
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

	private List<Expression> matchExpressionList() throws IOException {
		/* Match a list of expressions to pass to the tag */
		List<Expression> elist = new ArrayList<>(4);
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



	/** Match an expression, may begin with modifier tags TODO tags might require expression list or something? */
	private Optional<Expression> matchExpression() throws IOException {
		return Exp(0);
	}

	/* Precedence climbing implementation based on https://www.engr.mun.ca/~theo/Misc/exp_parsing.htm */

	private Optional<Expression> Exp(int precedence) throws IOException {
		 Optional<Expression> a = P();
		 if(a.isEmpty()) {
		 	return a;
		 }
		 Expression t = a.get();
		 while(peek().binaryOp().isPresent() && peek().binaryOp().get().precedence() >= precedence) {
		 	Token op = peek();
		 	int prec = switch(peek().binaryOp().get().assoc()) {
		 		case RIGHT -> peek().binaryOp().get().precedence();
		 		case LEFT -> peek().binaryOp().get().precedence() + 1;
		    };
		 	match();
		 	Optional<Expression> e = Exp(prec);
		 	t = new CallExpression(new NameSegment("TODO:binary-op-" + op),
			    t, e.orElseThrow(()
			    -> new ASTException("Binary expression missing right hand side: " + current)));
		 }
		 return Optional.of(t);
	}

	private Optional<Expression> P() throws IOException {
		if(peek().unaryOp().isPresent()) {
			Token op = peek();
			match();
			var t = Exp(op.unaryOp().get().precedence());
			return Optional.of(new CallExpression(new NameSegment("TODO:unary-minus-func"),
				t.orElseThrow(() -> new ASTException("Unary operation missing right hand side: " + current))));
		} else if(peek() == SimpleToken.OPEN_PAREN) {
			match();
			var t = Exp(0);
			match(SimpleToken.CLOSE_PAREN);
			return t;
		} else if(peek() instanceof Expression e) {
			match();
			return Optional.of(e);
		} else {
			/* TODO maybe this isn't an error, just an absence of expression? */
			//throw new ASTException("Not an expression " + current);
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

	private void throwUnknown() {
		throw new ASTException(current);
	}
}
