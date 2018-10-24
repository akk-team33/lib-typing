package de.team33.libs.typing.v3;

import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Optional;

class TypeVariableStage extends SingleStage {

    private final DefType<?> definite;

    TypeVariableStage(final TypeVariable<?> type, final Stage context) {
        final String name = type.getName();
        this.definite = Optional.ofNullable(context.getActualParameter(name))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Variable <%s> not found in parameters %s", name, context)));
    }

    @Override
    final Class<?> getUnderlyingClass() {
        return definite.getUnderlyingClass();
    }

    @Override
    final List<DefType<?>> getActualParameters() {
        return definite.getActualParameters();
    }
}
