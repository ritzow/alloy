package alloy.compiler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class RunCompiler {
	public static void main(String[] args) throws IOException {
		if(args.length >= 1) {
			/* Run a single script file which will initiate a potential compilation */
			AlloyEnvironment scriptEnv = new AlloyEnvironment();
			scriptEnv.execute(Path.of(args[0]),
				Arrays.copyOfRange(args, 0, args.length));
		} else {
			System.out.println("Usage: <script file>");
		}
	}
}
