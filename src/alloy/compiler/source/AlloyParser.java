package alloy.compiler.source;

import alloy.compiler.Environment;
import alloy.compiler.source.AlloyScanner.TokenResult;
import alloy.compiler.source.Token.NameSegment;
import alloy.compiler.source.Token.SimpleToken;
import alloy.compiler.model.*;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static alloy.compiler.source.Token.SimpleToken.DOT;

/** Recursive descent parser for the Alloy programming language
 * Parses a single finite length character sequence **/
public class AlloyParser {
	private static final int AVERAGE_NAME_LENGTH = 4;

	private final AlloyScanner scan;
	private final Environment env;
	private TokenResult current;

	public static List<AlloyModule> parse(Reader reader, Environment env) {
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
	public List<AlloyModule> parse() throws IOException {
		matchBlocks();
		match(SimpleToken.END);

		/* Read in any remaining tokens and print them out */
		if(peek() != SimpleToken.END) {
			System.out.println("Unparsed tokens:");
		}
		while(peek() != SimpleToken.END) {
			System.out.println(current = scan.next());
		}
		return List.of();
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
						matchBlocks();
						match(SimpleToken.CLOSE_BRACE);
						env.log("Sub end");
						/* TODO return with sub */
						return new Block(tags, Optional.empty());
					}
					case SEMICOLON -> {
						match();
						return new Block(tags, Optional.empty());
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
		Name name = matchName();
		List<Tag> tags = new ArrayList<>();

		env.modules().get(name).forEach((n, m) -> {
			Name tagName = n.relativize(name);
			/* TODO only add tags that are a full match */
			tags.addAll(m.tags().get(tagName).values());
		});

		switch(tags.size()) {
			case 1 -> {
				/* Feed expressions to tag handler */
				Tag tag = tags.iterator().next();
				tag.handle(matchExpressions());
				return tag;
			}
			case 0 -> throw new ASTException("No tag handler with name '"
				+ name + "'");
			default -> throw new ASTException("Ambiguous tag name "
				+ name + ": " + tags);

		}
	}

	private List<Expression> matchExpressions() throws IOException {
		List<Expression> elist = new ArrayList<>();

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

	/** Match an expression, may begin with modifier tags TODO tags might require expression list or something? **/
	private Optional<Expression> matchExpression() throws IOException {
		if(peek() instanceof Expression e) {
			/* Handle tokens that are already expressions (literal values) */
			match();
			return Optional.of(e);
		} else if (peek() instanceof NameSegment seg) {
			/* Match named something or other: function call, variable value, dereference, etc. */
			/* variablename[blah] alloy.base.Print(), fieldname.recordfield..valuefield.field...() */
			match();
			/* TODO lookup stuff that starts with seg */
			return Optional.of(matchReference(List.of()));
		} else if(peek() instanceof SimpleToken s) {
			switch(s) {
				case OPEN_PAREN -> {
					match();
					Optional<Expression> expr = matchExpression();
					match(SimpleToken.CLOSE_PAREN);
					return expr;
				}
				case TAG -> {
					/* TODO match tags and associate with expression */
					matchTag();
					throw new ASTException("not implemented");
				}
				default -> throw new ASTException("Not an expression start " + current);
			}
		} else {
			throw new ASTException("Not an expression start " + current);
		}
	}

	/* Match something ending in a name */
	private Expression matchReference(List<Object> choices) throws IOException {
		if(choices.isEmpty()) {
			throw new ASTException("No matches");
		}

		if(peek() instanceof SimpleToken tok) {
			switch(tok) {
				case DOT -> {
					/* Matched a double dereference:
					/* Narrow down the list of choices */
					/* TODO basically just need to add stuff to the chain */
					match();
					List<Object> newChoices = new ArrayList<>();
					if(peek() instanceof NameSegment seg) {
						/* TODO perform named dereference */
						for(var opt : choices) {
							/* TODO dereference this choice and see where it leads */
						}
					} else if(peek() instanceof SimpleToken tok2) {
						/* TODO perform anonymous dereference on all choices */
						switch(tok2) {
							case DOT -> {
								/* Anonymous dereference */

							}
							default -> throwUnknown(); /* Can't end an expression with a dot */
						}
					} /* else end of expression */
				}
				case OPEN_CHEVRON -> {
					/* generic type or function call */
					match();
					/* TODO match type parameter stuff */
					matchExpression();
					match(SimpleToken.CLOSE_CHEVRON);
					throw new ASTException("Not implemented");
				}
				case OPEN_PAREN -> {
					/* function call */
					match();
					matchExpression();
					match(SimpleToken.CLOSE_PAREN);
					throw new ASTException("Not implemented");
				}
				case OPEN_BRACKET -> {
					match();
					/* Match subscript parameters */
					matchExpressionList();
					match(SimpleToken.CLOSE_BRACKET);
					throw new ASTException("Not implemented");
				}
				default -> throwUnknown();
			}
		} else if(choices.size() == 1) {
			/* TODO turn only reference into expression */
			return (Expression)choices.iterator().next();
		} else {
			/* More than one choice */
			throw new ASTException("Ambiguous reference: " + choices);
		}
		return null;
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
