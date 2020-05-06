package de.team33.libs.typing.v3;

import java.util.Collections;
import java.util.List;

abstract class ArrayShape extends Shape {

    private static final List<String> FORMAL_PARAMETERS = Collections.singletonList("E");

    @Override
    final List<String> getFormalParameters() {
        return FORMAL_PARAMETERS;
    }

    @Override
    public final String toString() {
        return getActualParameters().get(0) + "[]";
    }
}
