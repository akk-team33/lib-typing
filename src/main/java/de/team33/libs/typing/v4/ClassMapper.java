package de.team33.libs.typing.v4;

import java.util.function.Function;

enum ClassMapper {

    CLASS(PlainClassShape::new),
    ARRAY(PlainArrayShape::new);

    private final Function<Class<?>, Shape> mapping;

    ClassMapper(final Function<Class<?>, Shape> mapping) {
        this.mapping = mapping;
    }

    static Shape map(final Class<?> underlyingClass) {
        return (underlyingClass.isArray() ? ARRAY : CLASS).mapping.apply(underlyingClass);
    }
}
