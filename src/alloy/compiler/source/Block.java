package alloy.compiler.source;

import alloy.compiler.SourceReconstructable;
import java.io.PrintStream;
import java.util.List;

public record Block(List<Tag> tags, List<Block> sub) implements SourceReconstructable {
	@Override
	public String toSource() {
		StringBuilder sb = new StringBuilder();
		for(var tag : tags) {
			sb.append(tag.toSource());
		}

		if(sub.isEmpty()) {
			sb.append(';');
		} else {
			sb.append(" {\n");

			for(var block : sub) {
				sb.append(block.toSource().indent(4));
			}

			sb.append('}');
		}
		return sb.toString();
	}
}
