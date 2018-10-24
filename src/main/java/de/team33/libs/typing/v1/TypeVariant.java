package de.team33.libs.typing.v1;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

enum TypeVariant {

    CLASS(
            type -> type instanceof Class<?>,
            (type, context) -> ClassVariant.toStage((Class<?>) type)),

    GENERIC_ARRAY(
            type -> type instanceof GenericArrayType,
            ((type, context) -> new GenericArrayStage((GenericArrayType) type, context))),

    PARAMETERIZED_TYPE(
            type -> type instanceof ParameterizedType,
            (type, context) -> new ParameterizedStage((ParameterizedType) type, context)),

    TYPE_VARIABLE(
            type -> type instanceof TypeVariable,
            (type, context) -> new TypeVariableStage((TypeVariable<?>) type, context));

    private final Predicate<Type> matching;
    private final BiFunction<Type, Stage, Stage> mapping;

    TypeVariant(final Predicate<Type> matching, final BiFunction<Type, Stage, Stage> mapping) {
        this.matching = matching;
        this.mapping = mapping;
    }

    static Stage toStage(final Type type, final Stage context) {
        return Stream.of(values())
                .filter(typeType -> typeType.matching.test(type)).findAny()
                .map(typeType -> typeType.mapping.apply(type, context))
                .orElseThrow(() -> new IllegalArgumentException("Unknown type of Type: " + type.getClass()));
    }
}
