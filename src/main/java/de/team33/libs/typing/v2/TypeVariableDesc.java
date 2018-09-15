package de.team33.libs.typing.v2;

import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class TypeVariableDesc extends TypeDesc {

    private final TypeDesc definite;

    TypeVariableDesc(final TypeVariable<?> type, final Map<String, TypeDesc> context) {
        final String name = type.getName();
        this.definite = Optional.ofNullable(context.get(name))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Variable <%s> not found in parameters %s", name, context)));
    }

    @Override
    public final Class<?> getUnderlyingClass() {
        return definite.getUnderlyingClass();
    }

    @Override
    public final List<String> getFormalParameters() {
        return definite.getFormalParameters();
    }

    @Override
    public final List<TypeDesc> getActualParameters() {
        return definite.getActualParameters();
    }
}
