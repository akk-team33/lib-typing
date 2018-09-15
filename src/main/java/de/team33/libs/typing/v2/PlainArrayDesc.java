package de.team33.libs.typing.v2;

import java.util.List;

import static java.util.Collections.singletonList;

class PlainArrayDesc extends ArrayDesc {

    private final Class<?> underlyingClass;
    private final List<TypeDesc> actualParameters;

    PlainArrayDesc(final Class<?> underlyingClass) {
        this.underlyingClass = underlyingClass;
        this.actualParameters = singletonList(ClassType.toDesc(underlyingClass.getComponentType()));
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
