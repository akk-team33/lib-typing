package de.team33.libs.typing.v1;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ParameterizedStage extends Stage {

    private final ParameterizedType type;
    private final ParameterMap context;

    ParameterizedStage(final ParameterizedType type, final ParameterMap context) {
        this.type = type;
        this.context = context;
    }

    private static DefType<?> newGeneric(final Stage stage) {
        return new DefType(stage) {
        };
    }

    @Override
    final Class<?> getUnderlyingClass() {
        return (Class<?>) type.getRawType();
    }

    @Override
    final ParameterMap getParameters() {
        final List<String> formal = Stream.of(((Class<?>) type.getRawType()).getTypeParameters())
                .map(TypeVariable::getName)
                .collect(Collectors.toList());
        final List<DefType<?>> actual = Stream.of(type.getActualTypeArguments())
                .map(type1 -> TypeVariant.stage(type1, context))
                .map(ParameterizedStage::newGeneric)
                .collect(Collectors.toList());
        return new ParameterMap(formal, actual);
    }
}