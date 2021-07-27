package alloy.compiler.source;

import alloy.compiler.source.Token.Associativity;
import java.util.function.BiFunction;

public record BinaryOp(int precedence, Associativity assoc, BiFunction<Expression, Expression, Expression> create) {}
