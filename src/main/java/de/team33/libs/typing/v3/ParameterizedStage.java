package de.team33.libs.typing.v3;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ParameterizedStage extends SingleStage {

    private final ParameterizedType type;
    private final Stage context;

    ParameterizedStage(final ParameterizedType type, final Stage context) {
        this.type = type;
        this.context = context;
    }

    @Override
    final Class<?> getUnderlyingClass() {
        return (Class<?>) type.getRawType();
    }

    @Override
    final List<Stage> getActualParameters() {
        return Stream.of(type.getActualTypeArguments())
                .map(type1 -> TypeVariant.toStage(type1, context))
                .collect(Collectors.toList());
    }
}
