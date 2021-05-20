package alloy.compiler;

import alloy.compiler.Token.SimpleToken;
import java.io.IOException;
import java.nio.file.Path;

public class RunCompiler {
	private static final Path TEST_PATH = Path.of("example/Base.alloy");

	public static void main(String[] args) throws IOException {
		AlloyScanner scan = new AlloyScanner(TEST_PATH);

		Token token;
		while((token = scan.next()) != SimpleToken.END) {
			System.out.println(token);
		}
	}
}
