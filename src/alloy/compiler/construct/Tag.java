package alloy.compiler.construct;

import alloy.compiler.symbol.Construct;

public class Tag implements Construct {
	private final String name;

	public Tag(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}
}
