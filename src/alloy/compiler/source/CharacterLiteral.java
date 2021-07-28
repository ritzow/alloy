package alloy.compiler.source;

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

	@Override
	public String toSource() {
		/* TODO this won't work for characters above 16bits */
		return "'" + Character.toString(codePoint) + "'";
	}
}
