package alloy.compiler;

import alloy.compiler.source.AlloyModule;

public interface Environment {
	void log(Exception e);
	void log(String message);
	NameDatabase<AlloyModule> modules();
}
