package de.team33.libs.typing.v4;

import java.util.function.Function;

enum ClassMapper {

    CLASS(PlainClassSetup::new),
    ARRAY(PlainArraySetup::new);

    private final Function<Class<?>, Setup> mapping;

    ClassMapper(final Function<Class<?>, Setup> mapping) {
        this.mapping = mapping;
    }

    static Setup map(final Class<?> underlyingClass) {
        return (underlyingClass.isArray() ? ARRAY : CLASS).mapping.apply(underlyingClass);
    }
}
