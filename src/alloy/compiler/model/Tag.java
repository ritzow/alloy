package alloy.compiler.model;

import java.util.List;

public abstract class Tag {

	public abstract void handle(List<Expression> exprs);

	public static class SimpleIntrinsicTag extends Tag {
		private final String name;
		public SimpleIntrinsicTag(String name) {
			this.name = name;
		}

		@Override
		public void handle(List<Expression> exprs) {
			for(var expr : exprs) {
				System.out.println("\t" + name + " " + expr);
			}
		}

		@Override
		public String toString() {
			return "SimpleIntrinsicTag[name=" + name + "]";
		}
	}
}
