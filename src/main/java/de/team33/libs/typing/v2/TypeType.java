package de.team33.libs.typing.v2;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

enum TypeType {

    CLASS(
            type -> type instanceof Class<?>,
            (type, map) -> ClassType.toDesc((Class<?>) type)),

    GENERIC_ARRAY(
            type -> type instanceof GenericArrayType,
            ((type, map) -> new GenericArrayDesc((GenericArrayType) type, map))
    ),

    PARAMETERIZED_TYPE(
            type -> type instanceof ParameterizedType,
            (type, map) -> new ParameterizedDesc((ParameterizedType) type, map)),

    TYPE_VARIABLE(
            type -> type instanceof TypeVariable,
            (type, map) -> new TypeVariableDesc((TypeVariable<?>) type, map));

    private final Predicate<Type> matching;
    private final BiFunction<Type, Map<String, TypeDesc>, TypeDesc> mapping;

    TypeType(final Predicate<Type> matching, final BiFunction<Type, Map<String, TypeDesc>, TypeDesc> mapping) {
        this.matching = matching;
        this.mapping = mapping;
    }

    static TypeDesc toDesc(final Type type, final Map<String, TypeDesc> parameters) {
        return Stream.of(TypeType.values())
                .filter(typeType -> typeType.matching.test(type)).findAny()
                .map(typeType -> typeType.mapping.apply(type, parameters))
                .orElseThrow(() -> new IllegalArgumentException("Unknown type of Type: " + type.getClass()));
    }
}
