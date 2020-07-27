package de.team33.libs.typing.v4;

import de.team33.libs.provision.v2.Lazy;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.List;

import static java.util.Collections.singletonList;

class GenericArraySetup extends ArraySetup {

    private final Class<?> primeClass;
    private final List<Setup> actualParameters;

    GenericArraySetup(final GenericArrayType type, final Setup context) {
        final Setup componentSetup = TypeMapper.map(type.getGenericComponentType(), context);
        this.actualParameters = singletonList(componentSetup);
        this.primeClass = Array.newInstance(componentSetup.getPrimeClass(), 0).getClass();
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
