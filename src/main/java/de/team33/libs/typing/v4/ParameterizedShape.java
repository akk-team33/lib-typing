package de.team33.libs.typing.v4;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ParameterizedShape extends DiscreteShape {

    private final Class<?> rawClass;
    private final List<Shape> actualParameters;

    ParameterizedShape(final ParameterizedType type, final Shape context) {
        this.rawClass = (Class<?>) type.getRawType();
        this.actualParameters = Collections.unmodifiableList(
                Stream.of(type.getActualTypeArguments())
                                 .map(argument -> TypeMapper.map(argument, context))
                                 .collect(Collectors.toList()));
    }

    @Override
    public final Class<?> getRawClass() {
        return rawClass;
    }

    @Override
    public final List<Shape> getActualParameters() {
        return actualParameters;
    }
}
