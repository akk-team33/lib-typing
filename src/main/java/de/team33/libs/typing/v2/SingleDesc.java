package de.team33.libs.typing.v2;

import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

abstract class SingleDesc extends TypeDesc {

    @Override
    public final List<String> getFormalParameters() {
        return Stream.of(getUnderlyingClass().getTypeParameters())
                .map(TypeVariable::getName)
                .collect(Collectors.toList());
    }

    @Override
    public final String toString() {
        final List<TypeDesc> actual = getActualParameters();
        return getUnderlyingClass().getSimpleName() + (
                actual.isEmpty() ? "" : actual.stream()
                        .map(TypeDesc::toString)
                        .collect(joining(", ", "<", ">")));
    }
}
