package alloy.compiler.model;

import java.util.function.Function;

public record UnaryOp(int precedence, Function<Expression, Expression> create) {}
