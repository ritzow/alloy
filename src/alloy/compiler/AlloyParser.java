package alloy.compiler;

import alloy.compiler.AlloyScanner.TokenResult;
import alloy.compiler.Token.NameSegment;
import alloy.compiler.Token.SimpleToken;
import alloy.compiler.ast.Block;
import alloy.compiler.ast.Name;
import alloy.compiler.ast.Tag;
import alloy.compiler.feature.TagHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** Recursive descent parser for the Alloy programming language **/
public class AlloyParser {
	private final AlloyScanner scan;
	private final AlloyEnvironment env;
	private TokenResult current;

	public AlloyParser(AlloyScanner scan, AlloyEnvironment env) throws IOException {
		this.scan = scan;
		this.env = env;
		this.current = scan.next();
	}

	public void parse() throws IOException {
		List<Block> blocks = matchBlocks();

		/* Read in any remaining tokens and print them out */
		if(current.token() != SimpleToken.END) {
			System.out.println("Unparsed tokens:");
		}
		while(current.token() != SimpleToken.END) {
			System.out.println(current = scan.next());
		}
	}

	/** A series of tags and expressions followed by a block or semicolon **/
	private List<Block> matchBlocks() throws IOException {
		List<Block> blocks = new ArrayList<>();
		Block b;
		do
		{
			b = matchBlock();
		} while(b != null);
		return blocks;
	}

	private Block matchBlock() throws IOException {
		List<Tag> tags = new ArrayList<>();
		do {
			/* Match tags up until semi or sub */
			if(current.token() instanceof SimpleToken tok) {
				switch(tok) {
					case TAG -> tags.add(matchTag());
					case OPEN_SUB -> {
						matchSub();
						/* TODO return with sub */
						return new Block(tags, Optional.empty());
					}
					case SEMICOLON -> {
						return new Block(tags, Optional.empty());
					}
					case END -> {
						return null;
					}
					default -> throw new ASTException(current);
				}
			} else {
				throw new ASTException(current);
			}
		} while(true);
	}

	/** Match anything in braces, matchBlocks might be able to replace this? **/
	private void matchSub() throws IOException {
		match(SimpleToken.OPEN_SUB);
		System.out.println("Sub start");
		while(current.token() != SimpleToken.CLOSE_SUB) {
			System.out.println("\t" + current);
			match();
		}
		match();
		System.out.println("Sub end");
	}

	/** Match a #, name, followed by any number of expressions **/
	private Tag matchTag() throws IOException {
		match(SimpleToken.TAG);
		/* Lookup tag name to find tag handler */
		Name name = matchName();
		TagHandler handler = env.lookupTag(name);

		if(handler != null) {
			handler.handle(List.of()); /* TODO pass in expressions */
		} else {
			throw new ASTException("No tag handler with name '" + name + "'");
		}

		/* Feed expressions to to tag handler */
		return null;
	}

	/** Match an expression **/
	private Object matchExpression() {
		throw new RuntimeException("Not implemented");
	}

	/** Match a name: character strings separated by dots **/
	private Name matchName() throws IOException {
		List<NameSegment> segments = new ArrayList<>(4);
		do {
			if(current.token() instanceof NameSegment seg) {
				match();
				segments.add(seg);
				if(current.token() != SimpleToken.DOT) {
					return new Name(segments);
				} else {
					match(SimpleToken.DOT);
				}
			} else {
				throw new ASTException("Expected name");
			}
		} while(true);
	}

	private void match() throws IOException {
		current = scan.next();
	}

	private void match(SimpleToken token) throws IOException {
		if(current.token() != token) {
			throw new ASTException(current);
		}
		current = scan.next();
	}
}
