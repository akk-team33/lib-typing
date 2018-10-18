package de.team33.libs.typing.v1;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;

import static java.util.Collections.singletonList;

class GenericArrayStage extends Stage {

    private final DefType<?> componentType;

    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    GenericArrayStage(final GenericArrayType type, final ParameterMap context) {
        this.componentType = new DefType(TypeVariant.stage(type.getGenericComponentType(), context)) {
        };
    }

    private static Class<?> arrayClass(final Class<?> componentClass) {
        return Array.newInstance(componentClass, 0).getClass();
    }

    static ParameterMap newArrayParameterMap(final DefType<?> componentType) {
        return new ParameterMap(
                singletonList("E"),
                singletonList(componentType)
        );
    }

    @Override
    final Class<?> getUnderlyingClass() {
        return arrayClass(componentType.getUnderlyingClass());
    }

    @Override
    final ParameterMap getParameters() {
        return newArrayParameterMap(componentType);
    }
}