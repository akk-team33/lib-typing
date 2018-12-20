package de.team33.libs.typing.v3;

import java.util.Collections;
import java.util.List;

class PlainArrayStage extends ArrayStage {

    private final Class<?> underlyingClass;

    PlainArrayStage(final Class<?> underlyingClass) {
        this.underlyingClass = underlyingClass;
    }

    @Override
    final Class<?> getUnderlyingClass() {
        return underlyingClass;
    }

    @Override
    final List<Stage> getActualParameters() {
        return Collections.singletonList(ClassVariant.toStage(underlyingClass.getComponentType()));
    }
}
