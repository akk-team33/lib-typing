package de.team33.libs.typing.v3;

import java.util.function.Function;

enum ClassVariant {

    CLASS(ClassShape::new),
    ARRAY(PlainArrayShape::new);

    private final Function<Class<?>, Shape> mapping;

    ClassVariant(final Function<Class<?>, Shape> mapping) {
        this.mapping = mapping;
    }

    static Shape toStage(final Class<?> underlyingClass) {
        return (underlyingClass.isArray() ? ARRAY : CLASS).mapping.apply(underlyingClass);
    }
}
