package alloy.compiler.model;

import alloy.compiler.model.Tag;
import java.util.List;
import java.util.Optional;

public record Block(List<Tag> tags, Optional<Object> sub) {

}
