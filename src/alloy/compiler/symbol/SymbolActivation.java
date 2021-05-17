package alloy.compiler.symbol;

import java.util.List;
import java.util.function.Function;

public final class SymbolActivation {
	private final List<SymbolActivation> next;
	private final Function<List<SymbolActivation>, Construct> onReduce;

	private SymbolActivation(Function<List<SymbolActivation>, Construct> onReduce, List<SymbolActivation> next) {
		this.onReduce = onReduce;
		this.next = next;
	}

	public static SymbolActivation of(Function<List<SymbolActivation>, Construct> onReduce, SymbolActivation... next) {
		return new SymbolActivation(onReduce, List.of(next));
	}

	public static SymbolActivation ofRun(Function<List<SymbolActivation>, Construct> onRead, TokenGroup tokens) {
		return null;
	}
}
