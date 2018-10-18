package de.team33.libs.typing.v3;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("MethodMayBeStatic")
abstract class Stage {

    final Class<?> getUnderlyingClass() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    abstract List<String> getFormalParameters();

    final Stream<Stage> getActualParameters() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
