package alloy.compiler;

import alloy.compiler.AlloyScanner.TokenResult;
import alloy.compiler.Token.NameSegment;
import alloy.compiler.Token.SimpleToken;
import alloy.compiler.model.Block;
import alloy.compiler.model.Expression;
import alloy.compiler.model.Name;
import alloy.compiler.model.AlloyModule;
import alloy.compiler.model.Tag;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Recursive descent parser for the Alloy programming language
 * Parses a single finite length character sequence **/
public class AlloyParser {
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
		List<Block> blocks = matchBlocks();
		match(SimpleToken.END);

		/* Read in any remaining tokens and print them out */
		if(current.token() != SimpleToken.END) {
			System.out.println("Unparsed tokens:");
		}
		while(current.token() != SimpleToken.END) {
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
			if(current.token() instanceof SimpleToken tok) {
				switch(tok) {
					case TAG -> tags.add(matchTag());
					case OPEN_BRACE -> {
						match();
						System.out.println("Sub start");
						matchBlocks();
						match(SimpleToken.CLOSE_BRACE);
						System.out.println("Sub end");
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
			Optional<Expression> e = tryMatchOuterExpression();
			if(e.isPresent()) {
				elist.add(e.get());
			} else {
				break;
			}
		} while(true);
		return elist;
	}

	/**
	 * Match an expression that can't start with tags
	 * Avoids ambiguity for tag expressions by requiring
	 * parentheses around any tags associated with this expression
	 **/
	private Optional<Expression> tryMatchOuterExpression() throws IOException {
		if(current.token() instanceof Expression e) {
			/* Handle tokens that are already expressions (literal values) */
			match();
			return Optional.of(e);
		} /* TODO parse expressions */ else {
			return Optional.empty();
		}
	}

	/** Match a name: character strings separated by dots **/
	private Name matchName() throws IOException {
		List<NameSegment> segments = new ArrayList<>(4);
		do {
			if(current.token() instanceof NameSegment seg) {
				match();
				segments.add(seg);
				if(current.token() != SimpleToken.DOT) {
					return Name.ofSegments(segments);
				} else {
					match(SimpleToken.DOT);
				}
			} else {
				throw new ASTException("Expected name");
			}
		} while(true);
	}

	private void match() throws IOException {
		if(current.token() == SimpleToken.END) {
			throw new ASTException("Went past end of file");
		}
		current = scan.next();
	}

	private void match(SimpleToken token) throws IOException {
		if(current.token() != token) {
			throw new ASTException(current);
		}
		current = scan.next();
	}
}
