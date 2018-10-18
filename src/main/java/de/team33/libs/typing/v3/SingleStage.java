package de.team33.libs.typing.v3;

import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;

class SingleStage extends Stage {

    @Override
    final List<String> getFormalParameters() {
        return unmodifiableList(
                Stream.of(getUnderlyingClass().getTypeParameters())
                        .map(TypeVariable::getName)
                        .collect(Collectors.toList())
        );
    }
}
