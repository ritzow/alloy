package alloy.compiler;

import alloy.compiler.Intrinsics.ModuleAlloyBase;
import alloy.compiler.Intrinsics.ModuleAlloySource;
import alloy.compiler.model.Name;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import alloy.compiler.model.AlloyModule;

/** A container for dynamically loading and manipulating modules **/
public class AlloyEnvironment implements Environment {
	private final NameDatabase<AlloyModule> names;

	public AlloyEnvironment() {
		this.names = new NameDatabase<>();
		this.names.lookupOrCreate(
			Name.of("alloy", "source"),
			ModuleAlloySource::new
		);
		this.names.lookupOrCreate(
			Name.of("alloy", "base"),
			ModuleAlloyBase::new
		);
	}

	/** Register some files, potentially built-in libraries **/
	public void load(Path... files) throws IOException {

	}

	/** Parse and execute an initial linker script file **/
	public void execute(Path file, String... args) throws IOException {
		/* TODO expose args using some compile-time source API */
		String extension = file.getFileName().toString();
		extension = extension.substring(extension.lastIndexOf('.'));
		if(extension.equals(".alloy")) {
			try(Reader in = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
				List<AlloyModule> modules = AlloyParser.parse(in, this);
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
