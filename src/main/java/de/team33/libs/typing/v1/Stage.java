package de.team33.libs.typing.v1;

import java.util.List;
import java.util.Optional;

abstract class Stage {

    abstract Class<?> getUnderlyingClass();

    abstract List<String> getFormalParameters();

    abstract List<DefType<?>> getActualParameters();

    final DefType<?> getActualParameter(final String name) {
        final List<String> formalParameters = getFormalParameters();
        return Optional.of(formalParameters.indexOf(name))
                .filter(index -> 0 <= index)
                .map(index -> getActualParameters().get(index))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("formal parameter <%s> not found in %s", name, formalParameters)));
    }
}
