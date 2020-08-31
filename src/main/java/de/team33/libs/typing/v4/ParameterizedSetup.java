package de.team33.libs.typing.v4;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ParameterizedSetup extends DiscreteSetup {

    ParameterizedSetup(final ParameterizedType type, final TypeSetup context) {
        super((Class<?>) type.getRawType(), Collections.unmodifiableList(
                Stream.of(type.getActualTypeArguments())
                      .map(argument -> TypeMapper.map(argument, context))
                      .collect(Collectors.toList())));
    }
}
