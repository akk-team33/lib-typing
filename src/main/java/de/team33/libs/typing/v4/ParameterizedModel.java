package de.team33.libs.typing.v4;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ParameterizedModel extends DiscreteModel {

    private final Class<?> rawClass;
    private final List<Model> actualParameters;

    ParameterizedModel(final ParameterizedType type, final Model context) {
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
    public final List<Model> getActualParameters() {
        return actualParameters;
    }
}
