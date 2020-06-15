package de.team33.libs.typing.v4;

import java.lang.reflect.TypeVariable;
import java.util.List;

class TypeVariableModel extends DiscreteModel {

    private final Model definite;

    TypeVariableModel(final TypeVariable<?> type, final Model context) {
        final String name = type.getName();
        this.definite = context.getActualParameter(name);
    }

    @Override
    public final Class<?> getRawClass() {
        return definite.getRawClass();
    }

    @Override
    public final List<Model> getActualParameters() {
        return definite.getActualParameters();
    }
}
