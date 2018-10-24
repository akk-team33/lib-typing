package de.team33.libs.typing.v3;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.List;

import static java.util.Collections.singletonList;

class GenericArrayStage extends ArrayStage {

    private final Type<?> componentType;

    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    GenericArrayStage(final GenericArrayType type, final Stage context) {
        this.componentType = new Type(TypeVariant.toStage(type.getGenericComponentType(), context)) {
        };
    }

    private static Class<?> arrayClass(final Class<?> componentClass) {
        return Array.newInstance(componentClass, 0).getClass();
    }

    @Override
    final Class<?> getUnderlyingClass() {
        return arrayClass(componentType.getUnderlyingClass());
    }

    @Override
    final List<Type<?>> getActualParameters() {
        return singletonList(componentType);
    }
}
