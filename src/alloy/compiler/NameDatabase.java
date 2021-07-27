package alloy.compiler;

import alloy.compiler.source.Name;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class NameDatabase<T> {
	private final Segment<T> lookup;

	@SafeVarargs
	public static <T> NameDatabase<T> of(Entry<Name, T>... entries) {
		var db = new NameDatabase<T>();
		for(var entry : entries) {
			db.lookupOrCreate(entry.getKey(), entry::getValue);
		}
		return db;
	}

	public NameDatabase() {
		this.lookup = new Segment<>();
	}

	private static final class Segment<T> {
		private final Map<String, Segment<T>> next;
		private T obj;

		Segment() {
			this.next = new HashMap<>();
		}

		@Override
		public String toString() {
			return "[content=" + obj + "]";
		}
	}

	/* TODO implement walk method */

	/** Lookup or create the exact match to the provided name **/
	public T lookupOrCreate(Name name, Supplier<T> sup) {
		Segment<T> cur = lookup;
		Iterator<String> it = name.iterator();

		do {
			cur = cur.next.computeIfAbsent(it.next(), s -> new Segment<>());
		} while(it.hasNext());

		if(cur.obj == null) {
			cur.obj = Objects.requireNonNull(sup.get());
		}
		return cur.obj;
	}

	/** Lookup the all matches for the provided name
	 *  Useful for finding fields and other things
	 *  in external modules **/
	public Map<Name, T> get(Name name) {
		Objects.requireNonNull(name);
		Iterator<String> it = name.iterator();
		Map<Name, T> matches = new TreeMap<>();
		List<String> sofar = new LinkedList<>();
		Segment<T> node = lookup;
		do {
			String segment = it.next();
			Segment<T> next = node.next.get(segment);
			if(next != null) {
				node = next;
				sofar.add(segment);
				if(node.obj != null) {
					matches.put(Name.of(sofar), node.obj);
				}
			} else {
				return matches;
			}
		} while(it.hasNext());
		return matches;
	}



	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(0, lookup.next, sb);
		return sb.toString();
	}

	private void toString(int level,
		Map<String, Segment<T>> cur, StringBuilder build) {
		for(var entry : cur.entrySet()) {
			build
				.append("   ".repeat(level))
				.append(entry.getKey())
				.append(" : ")
				.append(entry.getValue().obj)
				.append('\n');
			toString(level + 1, entry.getValue().next, build);
		}
	}
}
