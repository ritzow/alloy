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
			AlloyEnvironment scriptEnv = new AlloyEnvironment();
			scriptEnv.execute(Path.of(args[0]),
				Arrays.copyOfRange(args, 0, args.length));
//			System.out.println("Script Environment:");
//			System.out.println(scriptEnv);
		} else {
			System.out.println("Usage: <script file>");
		}
	}
}
