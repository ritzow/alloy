package alloy.compiler.ast;

import alloy.compiler.Token.NameSegment;
import java.util.Iterator;
import java.util.List;

public class Name implements Iterable<String> {
	private final List<String> segments;

	public Name(String... name) {
		this.segments = List.of(name);
	}

	public Name(List<NameSegment> name) {
		this.segments = name.stream().map(NameSegment::text).toList();
	}

	@Override
	public String toString() {
		return String.join(".", segments);
	}

	@Override
	public Iterator<String> iterator() {
		return segments.iterator();
	}
}
