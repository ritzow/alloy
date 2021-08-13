package alloy.compiler.env;

import alloy.compiler.NameDatabase;
import alloy.compiler.source.Tag;

/* A partially or fully loaded module */
public class AlloyModule {

	/** Define a new module stub. **/
	public AlloyModule() {

	}

	/** Potentially temporary until a more general system is created **/
	public NameDatabase<Tag> tags() {
		return null;
	}

}
