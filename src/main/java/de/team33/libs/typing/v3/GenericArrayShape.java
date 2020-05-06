package de.team33.libs.typing.v3;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.List;

import static java.util.Collections.singletonList;

class GenericArrayShape extends ArrayShape {

    private final Shape componentType;

    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    GenericArrayShape(final GenericArrayType type, final Shape context) {
        this.componentType = (TypeVariant.toStage(type.getGenericComponentType(), context));
    }

    private static Class<?> arrayClass(final Class<?> componentClass) {
        return Array.newInstance(componentClass, 0).getClass();
    }

    @Override
    public final Class getRawClass() {
        return arrayClass(componentType.getRawClass());
    }

    @Override
    public final List<Shape> getActualParameters() {
        return singletonList(componentType);
    }
}
