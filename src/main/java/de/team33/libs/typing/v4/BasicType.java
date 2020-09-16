package de.team33.libs.typing.v4;

import java.util.List;

abstract class BasicType extends RawType {

    private final Class<?> primeClass;
    private final List<RawType> actualParameters;

    BasicType(final Class<?> primeClass, final List<RawType> actualParameters) {
        this.primeClass = primeClass;
        this.actualParameters = actualParameters;
    }

    @Override
    public final Class<?> getPrimeClass() {
        return primeClass;
    }

    @Override
    public final List<RawType> getActualParameters() {
        return actualParameters;
    }
}
