package de.team33.libs.typing.v3;

import java.util.List;

import static java.util.Collections.emptyList;

class ClassStage extends SingleStage {

    private final Class<?> underlyingClass;

    ClassStage(final Class<?> underlyingClass) {
        this.underlyingClass = underlyingClass;
    }

    @Override
    final Class<?> getUnderlyingClass() {
        return underlyingClass;
    }

    @Override
    final List<Stage> getActualParameters() {
        return emptyList();
    }
}
