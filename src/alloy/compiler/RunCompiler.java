package alloy.compiler;

import java.io.IOException;
import java.nio.file.Path;

public class RunCompiler {
	private static final Path TEST_PATH = Path.of("example/Simple.alloy");

	public static void main(String[] args) throws IOException {
		AlloyScanner scan = new AlloyScanner(TEST_PATH);
		AlloyParser parser = new AlloyParser(scan, new AlloyEnvironment());
		parser.parse();
	}
}
