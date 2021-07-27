package alloy.compiler.source;

import java.util.List;

public record Tag(Name name, List<Expression> values) {}
