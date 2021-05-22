package alloy.compiler;

import alloy.compiler.AlloyScanner.TokenResult;

public class ASTException extends RuntimeException {
	public ASTException() {
	}

	public ASTException(String message) {
		super(message);
	}

	public ASTException(String message, Throwable cause) {
		super(message, cause);
	}

	public ASTException(Throwable cause) {
		super(cause);
	}

	public ASTException(TokenResult token) {
		super("Unexpected token "
			+ token.token() + " at line "
			+ token.context().lineNumber());
	}
}
