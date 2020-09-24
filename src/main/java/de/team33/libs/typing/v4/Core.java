package de.team33.libs.typing.v4;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

class Core extends AbstractList<Object> {

    private static final List<Function<Core, Object>> PROPERTIES = unmodifiableList(new ArrayList<>(asList(
            core -> core.primeClass,
            core -> core.actualParameters
    )));

    final Class<?> primeClass;
    final List<RawType> actualParameters;

    Core(final Class<?> primeClass, final List<RawType> actualParameters) {
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
