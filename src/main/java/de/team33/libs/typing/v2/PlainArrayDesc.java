package de.team33.libs.typing.v2;

import java.util.Collections;
import java.util.List;

class PlainArrayDesc extends ArrayDesc {

    private final Class<?> underlyingClass;
    private final List<TypeDesc> actualParameters;

    PlainArrayDesc(final Class<?> underlyingClass) {
        this.underlyingClass = underlyingClass;
        this.actualParameters = Collections.singletonList(PlainDesc.of(underlyingClass.getComponentType()));
    }

    @Override
    public final Class<?> getUnderlyingClass() {
        return underlyingClass;
    }

    @Override
    public final List<TypeDesc> getActualParameters() {
        return actualParameters;
    }
}
