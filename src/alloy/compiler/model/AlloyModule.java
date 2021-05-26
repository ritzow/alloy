package alloy.compiler.model;

import alloy.compiler.NameDatabase;

public interface AlloyModule {

	/** Potentially temporary until a more general system is created **/
	NameDatabase<Tag> tags();

	/*final class LoadedModule implements AlloyModule {
		@Override
		public Map<Name, Tag> lookupTag(Name name) {
			throw new ASTException("Not implemented");
		}
	}*/
}
