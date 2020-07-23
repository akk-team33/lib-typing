package de.team33.libs.typing.v4;

import java.lang.reflect.TypeVariable;
import java.util.List;

class TypeVariableSetup extends DiscreteSetup {

    private final Setup definite;

    TypeVariableSetup(final TypeVariable<?> type, final Setup context) {
        final String name = type.getName();
        this.definite = context.getActualParameter(name);
    }

    @Override
    public final Class<?> getPrimeClass() {
        return definite.getPrimeClass();
    }

    @Override
    public final List<Setup> getActualParameters() {
        return definite.getActualParameters();
    }
}
