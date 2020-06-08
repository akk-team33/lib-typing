package de.team33.libs.typing.v4;

import de.team33.libs.provision.v2.Lazy;

import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

abstract class DiscreteShape extends Shape {

    private final transient Lazy<String> stringView =
            new Lazy<>(() -> getRawClass().getSimpleName() + newActualParametersView());

    private final transient Lazy<List<String>> formalParameters =
            new Lazy<>(() -> unmodifiableList(
                    Stream.of(getRawClass().getTypeParameters())
                          .map(TypeVariable::getName)
                          .collect(Collectors.toList())));

    private String newActualParametersView() {
        return Optional.of(getActualParameters())
                       .filter(list -> 0 < list.size())
                       .map(list -> list.stream()
                                        .map(Shape::toString)
                                        .collect(joining(", ", "<", ">")))
                       .orElse("");
    }

    @Override
    public final List<String> getFormalParameters() {
        return formalParameters.get();
    }

    @Override
    public final String toString() {
        return stringView.get();
    }
}
