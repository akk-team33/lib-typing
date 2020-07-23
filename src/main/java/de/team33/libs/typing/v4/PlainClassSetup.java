package de.team33.libs.typing.v4;

import java.util.List;

import static java.util.Collections.emptyList;

class PlainClassSetup extends DiscreteSetup {

    private static final List<Setup> ACTUAL_PARAMETERS = emptyList();

    private final Class<?> rawClass;

    PlainClassSetup(final Class<?> rawClass) {
        this.rawClass = rawClass;
    }

    @Override
    public final Class<?> getPrimeClass() {
        return rawClass;
    }

    @Override
    public final List<Setup> getActualParameters() {
        return ACTUAL_PARAMETERS;
    }
}
