package de.team33.libs.typing.v4;

import java.util.function.Function;

enum ClassMapper {

    CLASS(PlainClassModel::new),
    ARRAY(PlainArrayModel::new);

    private final Function<Class<?>, Model> mapping;

    ClassMapper(final Function<Class<?>, Model> mapping) {
        this.mapping = mapping;
    }

    static Model map(final Class<?> underlyingClass) {
        return (underlyingClass.isArray() ? ARRAY : CLASS).mapping.apply(underlyingClass);
    }
}
