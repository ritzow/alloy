package alloy.compiler.feature;

import alloy.compiler.ast.Expression;
import java.util.List;

public abstract class TagHandler {

	public abstract void handle(List<Expression> exprs);

	public static class SimpleIntrinsicTag extends TagHandler {
		private final String name;
		public SimpleIntrinsicTag(String name) {
			this.name = name;
		}

		@Override
		public void handle(List<Expression> exprs) {
			System.out.println(name + " intrinsic handle " + exprs);
		}
	}
}
