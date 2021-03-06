package alloy.compiler;

import alloy.compiler.env.AlloyModule;
import alloy.compiler.source.AlloyParser;
import alloy.compiler.source.Block;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A container for dynamically loading and manipulating modules
 *
 * An alloy environment is a compiler context that has at most
 * one instance of any defined language construct specifications.
 * That means there cannot be more than one
 **/
public class AlloyEnvironment implements Environment {
	private final NameDatabase<AlloyModule> names;

	public AlloyEnvironment() {
		this.names = new NameDatabase<>();
	}

	/** Register some files, potentially built-in libraries **/
//	public void load(Path... files) throws IOException {
//
//	}

	/** Parse and execute an initial linker script file **/
	public void execute(Path file, String... args) throws IOException {
		/* TODO expose args using some compile-time source API */
		String extension = file.getFileName().toString();
		extension = extension.substring(extension.lastIndexOf('.'));
		if(extension.equals(".alloy")) {
			try(Reader in = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
				List<Block> modules = AlloyParser.parse(in, this);

				for(Block block : modules) {
					System.out.println(block.toSource());
				}

				modules.clear();

				if(modules.size() > 1) {
					log("Input script file must contain a single module");
				} else {
					/* TODO find and execute main method in module, pass in args */
				}
			}
		} else if(extension.equals(".alloy-lib")) {
			log("Executing Alloy binaries is currently not supported");
		} else {
			System.out.println("Unknown file extension '" + extension + "'");
		}
	}

	@Override
	public void log(Exception e) {
		e.printStackTrace();
	}

	@Override
	public void log(String message) {
		System.err.println(message);
	}

	/** The global environment database for looking up external references
	 **/
	@Override
	public NameDatabase<AlloyModule> modules() {
		return names;
	}

	@Override
	public String toString() {
		return names.toString();
	}

	/* TODO functions to enumerate modules and other things using iterators for the
	 * purpose of global optimization */


}
