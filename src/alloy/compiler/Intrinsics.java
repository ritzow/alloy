package alloy.compiler;

import alloy.compiler.model.Name;
import alloy.compiler.model.Tag;
import alloy.compiler.model.AlloyModule;
import alloy.compiler.model.Tag.SimpleIntrinsicTag;
import java.util.Map;

public class Intrinsics {
	public static AlloyModule generateBootstrap() {
		return new ModuleAlloySource();
	}

	static class ModuleAlloySource implements AlloyModule {
		private final NameDatabase<Tag> tags = NameDatabase.of(
			Map.entry(Name.of("view"),
				new SimpleIntrinsicTag("alloy.source.view"))
		);

		@Override
		public NameDatabase<Tag> tags() {
			return tags;
		}

		@Override
		public String toString() {
			return tags.toString();
		}
	}

	static class ModuleAlloyBase implements AlloyModule {
		private final NameDatabase<Tag> tags = NameDatabase.of(
			Map.entry(Name.of("module"), new SimpleIntrinsicTag("alloy.base.module"))
		);

		@Override
		public NameDatabase<Tag> tags() {
			return tags;
		}
	}
}