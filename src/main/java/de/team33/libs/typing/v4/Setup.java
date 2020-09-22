package de.team33.libs.typing.v4;

import java.util.*;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

class Setup extends AbstractList<Object> {

    private static final List<Function<Setup, Object>> PROPERTIES = unmodifiableList(new ArrayList<>(asList(
            setup -> setup.primeClass,
            setup -> setup.actualParameters
    )));

    final Class<?> primeClass;
    final List<RawType> actualParameters;

    Setup(final Class<?> primeClass, final List<RawType> actualParameters) {
        this.primeClass = primeClass;
        this.actualParameters = unmodifiableList(new ArrayList<>(actualParameters));
    }

    @Override
    public final Object get(final int index) {
        return PROPERTIES.get(index).apply(this);
    }

    @Override
    public final int size() {
        return PROPERTIES.size();
    }
}
