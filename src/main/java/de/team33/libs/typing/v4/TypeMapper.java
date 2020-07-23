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
            ((type, context) -> new GenericArraySetup((GenericArrayType) type, context))),

    PARAMETERIZED_TYPE(
            type -> type instanceof ParameterizedType,
            (type, context) -> new ParameterizedSetup((ParameterizedType) type, context)),

    TYPE_VARIABLE(
            type -> type instanceof TypeVariable,
            (type, context) -> new TypeVariableSetup((TypeVariable<?>) type, context));

    private final Predicate<Type> matching;
    private final BiFunction<Type, Setup, Setup> mapping;

    TypeMapper(final Predicate<Type> matching, final BiFunction<Type, Setup, Setup> mapping) {
        this.matching = matching;
        this.mapping = mapping;
    }

    static Setup map(final Type type, final Setup context) {
        return Stream.of(values())
                .filter(typeType -> typeType.matching.test(type)).findAny()
                .map(typeType -> typeType.mapping.apply(type, context))
                .orElseThrow(() -> new IllegalArgumentException("Unknown type of Type: " + type.getClass()));
    }
}
