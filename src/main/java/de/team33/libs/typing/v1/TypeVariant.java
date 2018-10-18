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
            (type, map) -> new ClassStage((Class<?>) type)),

    GENERIC_ARRAY(
            type -> type instanceof GenericArrayType,
            ((type, map) -> new GenericArrayStage((GenericArrayType) type, map))
    ),

    PARAMETERIZED_TYPE(
            type -> type instanceof ParameterizedType,
            (type, map) -> new ParameterizedStage((ParameterizedType) type, map)),

    TYPE_VARIABLE(
            type -> type instanceof TypeVariable,
            (type, map) -> new TypeVariableStage((TypeVariable<?>) type, map));

    private final Predicate<Type> matching;
    private final BiFunction<Type, ParameterMap, Stage> mapping;

    TypeVariant(final Predicate<Type> matching, final BiFunction<Type, ParameterMap, Stage> mapping) {
        this.matching = matching;
        this.mapping = mapping;
    }

    static Stage stage(final Type type, final ParameterMap parameters) {
        return Stream.of(values())
                .filter(typeType -> typeType.matching.test(type)).findAny()
                .map(typeType -> typeType.mapping.apply(type, parameters))
                .orElseThrow(() -> new IllegalArgumentException("Unknown type of Type: " + type.getClass()));
    }
}
