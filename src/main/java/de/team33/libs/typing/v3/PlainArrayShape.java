package de.team33.libs.typing.v3;

import java.util.Collections;
import java.util.List;

class PlainArrayShape extends ArrayShape {

    private final Class<?> underlyingClass;

    PlainArrayShape(final Class<?> underlyingClass) {
        this.underlyingClass = underlyingClass;
    }

    @Override
    public final Class<?> getUnderlyingClass() {
        return underlyingClass;
    }

    @Override
    final List<Shape> getActualParameters() {
        return Collections.singletonList(ClassVariant.toStage(underlyingClass.getComponentType()));
    }
}
