package de.team33.libs.typing.v3;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ParameterizedShape extends SingleShape {

    private final ParameterizedType type;
    private final Shape context;

    ParameterizedShape(final ParameterizedType type, final Shape context) {
        this.type = type;
        this.context = context;
    }

    @Override
    public final Class<?> getUnderlyingClass() {
        return (Class<?>) type.getRawType();
    }

    @Override
    final List<Shape> getActualParameters() {
        return Stream.of(type.getActualTypeArguments())
                .map(type1 -> TypeVariant.toStage(type1, context))
                .collect(Collectors.toList());
    }
}
