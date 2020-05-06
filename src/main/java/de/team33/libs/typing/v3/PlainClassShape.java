package de.team33.libs.typing.v3;

import java.util.List;

import static java.util.Collections.emptyList;

class PlainClassShape extends DiscreteShape {

    private final Class<?> underlyingClass;

    PlainClassShape(final Class<?> underlyingClass) {
        this.underlyingClass = underlyingClass;
    }

    @Override
    public final Class getRawClass() {
        return underlyingClass;
    }

    @Override
    public final List<Shape> getActualParameters() {
        return emptyList();
    }
}
