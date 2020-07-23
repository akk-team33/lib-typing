package de.team33.libs.typing.v4;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ParameterizedSetup extends DiscreteSetup {

    private final Class<?> primeClass;
    private final List<Setup> actualParameters;

    ParameterizedSetup(final ParameterizedType type, final Setup context) {
        this.primeClass = (Class<?>) type.getRawType();
        this.actualParameters = Collections.unmodifiableList(
                Stream.of(type.getActualTypeArguments())
                                 .map(argument -> TypeMapper.map(argument, context))
                                 .collect(Collectors.toList()));
    }

    @Override
    public final Class<?> getPrimeClass() {
        return primeClass;
    }

    @Override
    public final List<Setup> getActualParameters() {
        return actualParameters;
    }
}
