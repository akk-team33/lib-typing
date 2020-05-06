package de.team33.libs.typing.v3;

import java.util.List;

import static java.util.Collections.emptyList;

class ClassShape extends SingleShape {

    private final Class<?> underlyingClass;

    ClassShape(final Class<?> underlyingClass) {
        this.underlyingClass = underlyingClass;
    }

    @Override
    public final Class<?> getUnderlyingClass() {
        return underlyingClass;
    }

    @Override
    final List<Shape> getActualParameters() {
        return emptyList();
    }
}
