package de.team33.libs.typing.v2;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class GenericArrayDesc extends ArrayDesc {

    private final TypeDesc componentType;
    private final Class<?> underlyingClass;

    GenericArrayDesc(final GenericArrayType type, final Map<String, TypeDesc> context) {
        this.componentType = TypeType.toDesc(type.getGenericComponentType(), context);
        this.underlyingClass = Array.newInstance(componentType.getUnderlyingClass(), 0).getClass();
    }

    @Override
    public final Class<?> getUnderlyingClass() {
        return underlyingClass;
    }

    @Override
    public final List<TypeDesc> getActualParameters() {
        return Collections.singletonList(componentType);
    }
}
