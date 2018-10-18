package de.team33.libs.typing.v1;

import java.lang.reflect.TypeVariable;
import java.util.Optional;

class TypeVariableStage extends Stage {

    private final DefType<?> definite;

    TypeVariableStage(final TypeVariable<?> type, final ParameterMap context) {
        final String name = type.getName();
        this.definite = Optional.ofNullable(context.get(name))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Variable <%s> not found in parameters %s", name, context)));
    }

    @Override
    final Class<?> getUnderlyingClass() {
        return definite.getUnderlyingClass();
    }

    @Override
    final ParameterMap getParameters() {
        return new ParameterMap(definite.getFormalParameters(), definite.getActualParameters());
    }
}
