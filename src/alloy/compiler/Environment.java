package alloy.compiler;

import alloy.compiler.model.AlloyModule;

public interface Environment {
	void log(Exception e);
	void log(String message);
	NameDatabase<AlloyModule> modules();
}
