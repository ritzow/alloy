package alloy.compiler.model;

import java.util.List;

public record Tag(Name name, List<Expression> values) {}
