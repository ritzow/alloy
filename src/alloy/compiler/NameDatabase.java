package alloy.compiler;

import alloy.compiler.Token.NameSegment;
import alloy.compiler.ast.Name;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NameDatabase<T> {
	private final Segment<T> lookup;

	public NameDatabase() {
		this.lookup = new Segment<>();
	}

	private static class Segment<T> {
		private final Map<String, Segment<T>> next;
		private T obj;

		Segment() {
			this.next = new HashMap<>();
		}

		@Override
		public String toString() {
			return "[" + obj + " " + next + "]";
		}
	}

	public boolean add(T obj, NameSegment... name) {
		Segment<T> cur = lookup;
		int i = 0;

		do {
			cur = cur.next.computeIfAbsent(name[i].text(), s -> new Segment<>());
			i++;
		} while(i < name.length);

		if(cur.obj == null) {
			cur.obj = obj;
			return true;
		} else {
			return false;
		}
	}

	public T get(Name name) {
		Segment<T> cur = lookup;
		Iterator<String> it = name.iterator();
		do {
			cur = cur.next.get(it.next());
			if(cur == null) {
				return null;
			}
		} while(it.hasNext());
		return cur.obj;
	}

	@Override
	public String toString() {
		return "NameDatabase" + lookup;
	}
}
