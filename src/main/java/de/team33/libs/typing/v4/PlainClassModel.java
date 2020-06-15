package de.team33.libs.typing.v4;

import java.util.List;

import static java.util.Collections.emptyList;

class PlainClassModel extends DiscreteModel {

    private static final List<Model> ACTUAL_PARAMETERS = emptyList();

    private final Class<?> rawClass;

    PlainClassModel(final Class<?> rawClass) {
        this.rawClass = rawClass;
    }

    @Override
    public final Class<?> getRawClass() {
        return rawClass;
    }

    @Override
    public final List<Model> getActualParameters() {
        return ACTUAL_PARAMETERS;
    }
}
