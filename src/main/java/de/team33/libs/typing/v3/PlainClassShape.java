package de.team33.libs.typing.v3;

import java.util.List;

import static java.util.Collections.emptyList;

class PlainClassShape extends DiscreteShape {

    private static final List<Shape> ACTUAL_PARAMETERS = emptyList();

    private final Class<?> rawClass;

    PlainClassShape(final Class<?> rawClass) {
        this.rawClass = rawClass;
    }

    @Override
    public final Class<?> getRawClass() {
        return rawClass;
    }

    @Override
    public final List<Shape> getActualParameters() {
        return ACTUAL_PARAMETERS;
    }
}
