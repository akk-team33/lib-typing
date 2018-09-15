package de.team33.libs.typing.v2;

import java.util.List;

import static java.util.Collections.emptyList;

class ClassDesc extends TypeDesc {

    private final Class<?> underlyingClass;

    ClassDesc(final Class<?> underlyingClass) {
        this.underlyingClass = underlyingClass;
    }

    @Override
    public final Class<?> getUnderlyingClass() {
        return underlyingClass;
    }

    @Override
    public final List<String> getFormalParameters() {
        return emptyList();
    }

    @Override
    public final List<TypeDesc> getActualParameters() {
        return emptyList();
    }
}
