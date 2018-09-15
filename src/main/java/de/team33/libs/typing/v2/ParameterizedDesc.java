package de.team33.libs.typing.v2;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;

class ParameterizedDesc extends TypeDesc {

    private final ParameterizedType type;
    private final List<String> formalParameters;
    private final List<TypeDesc> actualParameters;

    ParameterizedDesc(final ParameterizedType type, final Map<String, TypeDesc> context) {
        this.type = type;
        this.formalParameters = Stream.of(((Class<?>) type.getRawType()).getTypeParameters())
                .map(TypeVariable::getName)
                .collect(Collectors.toList());
        this.actualParameters = Stream.of(type.getActualTypeArguments())
                .map(type1 -> TypeType.toDesc(type1, context))
                .collect(Collectors.toList());
    }

    @Override
    public final Class<?> getUnderlyingClass() {
        return (Class<?>) type.getRawType();
    }

    @Override
    public final List<String> getFormalParameters() {
        return unmodifiableList(formalParameters);
    }

    @Override
    public final List<TypeDesc> getActualParameters() {
        return unmodifiableList(actualParameters);
    }
}
