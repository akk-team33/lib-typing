package de.team33.libs.typing.v4;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ParameterizedType extends DiscreteType {

    ParameterizedType(final java.lang.reflect.ParameterizedType type, final RawType context) {
        super((Class<?>) type.getRawType(), Collections.unmodifiableList(
                Stream.of(type.getActualTypeArguments())
                      .map(argument -> TypeMapper.map(argument, context))
                      .collect(Collectors.toList())));
    }
}
