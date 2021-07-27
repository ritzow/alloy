package alloy.compiler.model;

import alloy.compiler.source.ASTException;
import alloy.compiler.source.NameSegment;
import java.util.*;

public final class Name implements Iterable<String>, Expression, Comparable<Name> {
	/* TODO replace with array */
	private final List<String> segments;

	public static Name of(String... name) {
		if(name.length < 1) {
			throw new IllegalArgumentException("Name cannot be empty");
		}
		return new Name(name);
	}

	public static Name ofSegments(List<NameSegment> name) {
		if(name.size() < 1) {
			throw new IllegalArgumentException("Name cannot be empty");
		}
		return new Name(name.stream().map(NameSegment::text).toList());
	}

	public static Name of(List<String> name) {
		if(name.size() < 1) {
			throw new IllegalArgumentException("Name cannot be empty");
		}
		return new Name(new ArrayList<>(name));
	}

	private Name(List<String> name) {
		this.segments = name;
	}

	private Name(String... name) {
		this.segments = List.of(name);
	}

	public Name relativize(Name other) {
		ListIterator<String>
			s1 = segments.listIterator(),
			s2 = other.segments.listIterator();
		if(other.length() <= length()) {
			throw new ASTException("Other name "
				+ other + " not longer than " + this);
		}

		while(s1.hasNext()) {
			if(!s1.next().equals(s2.next())) {
				throw new ASTException("Differing Name prefixes "
					+ this + " and " + other);
			}
		}

		List<String> name = new LinkedList<>();
		s2.forEachRemaining(name::add);
		return Name.of(name);
	}

	public int length() {
		return segments.size();
	}

	@Override
	public String toString() {
		return String.join(".", segments);
	}

	@Override
	public Iterator<String> iterator() {
		return segments.iterator();
	}

	@Override
	public Type type() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int compareTo(Name o) {
		Iterator<String> s1 = segments.iterator(), s2 = o.iterator();
		do {
			if(s1.hasNext() && !s2.hasNext()) {
				return -1;
			} else if(!s1.hasNext() && s2.hasNext()) {
				return 1;
			} else if(s1.hasNext() && s2.hasNext()) {
				int diff = s1.next().compareTo(s2.next());
				if(diff != 0) {
					return diff;
				} //else continue
			} else {
				return 0;
			}
		} while(true);
	}
}
