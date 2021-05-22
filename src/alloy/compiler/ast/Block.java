package alloy.compiler.ast;

import java.util.List;
import java.util.Optional;

public record Block(List<Tag> tags, Optional<Object> sub) {

}
