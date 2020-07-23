package de.team33.libs.typing.v4;

import de.team33.libs.provision.v2.Lazy;

import java.util.List;

import static java.util.Collections.singletonList;

class PlainArraySetup extends ArraySetup {

    private final Class<?> rawClass;

    private final transient Lazy<List<Setup>> actualParameters =
            new Lazy<>(() -> singletonList(ClassMapper.map(getPrimeClass().getComponentType())));

    PlainArraySetup(final Class<?> rawClass) {
        this.rawClass = rawClass;
    }

    @Override
    public final Class<?> getPrimeClass() {
        return rawClass;
    }

    @Override
    public final List<Setup> getActualParameters() {
        return actualParameters.get();
    }
}
