package de.team33.libs.typing.v1;

import java.util.function.Function;

enum ClassVariant {

    CLASS(ClassStage::new),
    ARRAY(PlainArrayStage::new);

    private final Function<Class<?>, Stage> mapping;

    ClassVariant(final Function<Class<?>, Stage> mapping) {
        this.mapping = mapping;
    }

    static Stage toStage(final Class<?> underlyingClass) {
        return (underlyingClass.isArray() ? ARRAY : CLASS).mapping.apply(underlyingClass);
    }
}
