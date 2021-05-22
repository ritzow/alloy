package alloy.compiler;

import alloy.compiler.Token.NameSegment;
import alloy.compiler.ast.Name;
import alloy.compiler.feature.TagHandler;
import alloy.compiler.feature.TagHandler.SimpleIntrinsicTag;

public class AlloyEnvironment {
	private final NameDatabase<TagHandler> names;

	public AlloyEnvironment() {
		this.names = new NameDatabase<>();
		this.names.add(new SimpleIntrinsicTag("alloy.source.example"),
			new NameSegment("alloy"),
			new NameSegment("source"),
			new NameSegment("example"));
		this.names.add(new SimpleIntrinsicTag("alloy.source.test"),
			new NameSegment("alloy"),
			new NameSegment("source"),
			new NameSegment("test"));
	}

	public TagHandler lookupTag(Name name) {
		return names.get(name);
	}
}
