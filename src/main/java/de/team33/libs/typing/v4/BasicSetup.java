package de.team33.libs.typing.v4;

import java.util.List;

abstract class BasicSetup extends TypeSetup {

    private final Class<?> primeClass;
    private final List<TypeSetup> actualParameters;

    BasicSetup(final Class<?> primeClass, final List<TypeSetup> actualParameters) {
        this.primeClass = primeClass;
        this.actualParameters = actualParameters;
    }

    @Override
    public final Class<?> getPrimeClass() {
        return primeClass;
    }

    @Override
    public final List<TypeSetup> getActualParameters() {
        return actualParameters;
    }
}
