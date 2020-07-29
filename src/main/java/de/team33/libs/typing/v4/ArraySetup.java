package de.team33.libs.typing.v4;

import java.util.Collections;
import java.util.List;

abstract class ArraySetup extends BasicSetup {

    private static final List<String> FORMAL_PARAMETERS = Collections.singletonList("E");

    private final transient Lazy<String> stringView = new Lazy<>(() -> getActualParameters().get(0) + "[]");

    ArraySetup(final Class<?> primeClass, final List<Setup> actualParameters) {
        super(primeClass, actualParameters);
    }

    @Override
    public final List<String> getFormalParameters() {
        return FORMAL_PARAMETERS;
    }

    @Override
    public final String toString() {
        return stringView.get();
    }
}
