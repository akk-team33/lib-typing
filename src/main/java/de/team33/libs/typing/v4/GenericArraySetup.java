package de.team33.libs.typing.v4;

import de.team33.libs.provision.v2.Lazy;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.List;

import static java.util.Collections.singletonList;

class GenericArraySetup extends ArraySetup {

    private final Setup componentSetup;
    private final transient Lazy<Class<?>> rawClass = new Lazy<>(this::newRawClass);

    GenericArraySetup(final GenericArrayType type, final Setup context) {
        this.componentSetup = TypeMapper.map(type.getGenericComponentType(), context);
    }

    private Class<?> newRawClass() {
        return Array.newInstance(componentSetup.getPrimeClass(), 0).getClass();
    }

    @Override
    public final Class<?> getPrimeClass() {
        return rawClass.get();
    }

    @Override
    public final List<Setup> getActualParameters() {
        return singletonList(componentSetup);
    }
}
