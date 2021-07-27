package alloy.compiler.source;

import java.util.function.Function;

public record UnaryOp(int precedence, Function<Expression, Expression> create) {}
