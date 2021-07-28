package alloy.compiler.source;

import alloy.compiler.SourceReconstructable;
import java.util.List;

public record Tag(Name name, List<Expression> values) implements SourceReconstructable {

	@Override
	public String toSource() {
		StringBuilder sb = new StringBuilder();
		sb.append('#').append(name);
		for(var expr : values) {
			sb.append(' ').append(expr.toSource());
		}
		return sb.toString();
	}
}
