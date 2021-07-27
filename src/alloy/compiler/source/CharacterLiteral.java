package alloy.compiler.source;

import alloy.compiler.model.Expression;
import alloy.compiler.model.Type;

public final record CharacterLiteral(int codePoint) implements Token, Expression {
	@Override
	public String toString() {
		return "CharacterLiteral[" +
			"codePoint=" + (Character.isSpaceChar(codePoint)
			? ("0x" + Integer.toHexString(codePoint)) : (
			'\'' + Character.toString(codePoint)) + '\'') +
			" (" + Character.getName(codePoint) + ")]";
	}

	@Override
	public Type type() {
		throw new RuntimeException("Not implemented");
	}
}
