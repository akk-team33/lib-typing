package de.team33.libs.typing.v2;

import java.util.function.Function;

enum ClassType {

    CLASS(ClassDesc::new),
    ARRAY(PlainArrayDesc::new);

    private final Function<Class<?>, TypeDesc> mapping;

    ClassType(final Function<Class<?>, TypeDesc> mapping) {
        this.mapping = mapping;
    }

    static TypeDesc toDesc(final Class<?> type) {
        return (type.isArray() ? ARRAY : CLASS).mapping.apply(type);
    }
}
