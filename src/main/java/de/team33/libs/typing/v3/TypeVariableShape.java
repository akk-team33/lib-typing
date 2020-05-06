package de.team33.libs.typing.v3;

import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Optional;

class TypeVariableShape extends SingleShape {

    private final Shape definite;

    TypeVariableShape(final TypeVariable<?> type, final Shape context) {
        final String name = type.getName();
        this.definite = Optional.ofNullable(context.getActualParameter(name))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Variable <%s> not found in parameters %s", name, context)));
    }

    @Override
    public final Class<?> getUnderlyingClass() {
        return definite.getUnderlyingClass();
    }

    @Override
    final List<Shape> getActualParameters() {
        return definite.getActualParameters();
    }
}
