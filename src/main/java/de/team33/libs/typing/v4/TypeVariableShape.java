package de.team33.libs.typing.v4;

import java.lang.reflect.TypeVariable;
import java.util.List;

class TypeVariableShape extends DiscreteShape {

    private final Shape definite;

    TypeVariableShape(final TypeVariable<?> type, final Shape context) {
        final String name = type.getName();
        this.definite = context.getActualParameter(name);
    }

    @Override
    public final Class<?> getRawClass() {
        return definite.getRawClass();
    }

    @Override
    public final List<Shape> getActualParameters() {
        return definite.getActualParameters();
    }
}
