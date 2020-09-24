package de.team33.libs.typing.v4;

import java.util.List;

abstract class BasicType extends RawType {

    private final Core core;

    BasicType(final Class<?> primeClass, final List<RawType> actualParameters) {
        this.core = new Core(primeClass, actualParameters);
    }

    @Override
    public final Class<?> getPrimeClass() {
        return core.primeClass;
    }

    @Override
    public final List<RawType> getActualParameters() {
        return core.actualParameters;
    }

    @Override
    final Core getCore() {
        return core;
    }
}
