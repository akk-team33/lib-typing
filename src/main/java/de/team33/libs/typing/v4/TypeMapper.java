package de.team33.libs.typing.v4;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

enum TypeMapper {

    CLASS(
            type -> type instanceof Class<?>,
            (type, context) -> ClassMapper.map((Class<?>) type)),

    GENERIC_ARRAY(
            type -> type instanceof GenericArrayType,
            ((type, context) -> new GenericArrayModel((GenericArrayType) type, context))),

    PARAMETERIZED_TYPE(
            type -> type instanceof ParameterizedType,
            (type, context) -> new ParameterizedModel((ParameterizedType) type, context)),

    TYPE_VARIABLE(
            type -> type instanceof TypeVariable,
            (type, context) -> new TypeVariableModel((TypeVariable<?>) type, context));

    private final Predicate<Type> matching;
    private final BiFunction<Type, Model, Model> mapping;

    TypeMapper(final Predicate<Type> matching, final BiFunction<Type, Model, Model> mapping) {
        this.matching = matching;
        this.mapping = mapping;
    }

    static Model map(final Type type, final Model context) {
        return Stream.of(values())
                .filter(typeType -> typeType.matching.test(type)).findAny()
                .map(typeType -> typeType.mapping.apply(type, context))
                .orElseThrow(() -> new IllegalArgumentException("Unknown type of Type: " + type.getClass()));
    }
}
