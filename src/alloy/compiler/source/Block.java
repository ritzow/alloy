package alloy.compiler.source;

import java.io.PrintStream;
import java.util.List;

public record Block(List<Tag> tags, List<Block> sub) {
	public void write(PrintStream out) {
		write(out, 0);
	}

	private void write(PrintStream out, int indent) {
		for(Tag tag : tags) {
			out.print('#');
			out.print(tag.name());
			//out.print(tag.va)
		}
	}
}
