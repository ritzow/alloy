package alloy.compiler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class RunCompiler {
	public static void main(String[] args) throws IOException {

		//Name name = Name.of("a", "b", "c");
		//Name name2 = Name.of("a", "b", "c", "d", "e");
		//System.out.println(name.relativize(name2));

		//System.exit(0);

		if(args.length >= 1) {
			/* Run a single script file which will initiate a potential compilation */
			new AlloyEnvironment().execute(Path.of(args[0]),
				Arrays.copyOfRange(args, 0, args.length));
		} else {
			System.out.println("Usage: <script file>");
		}
	}
}
