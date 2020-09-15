package de.team33.libs.typing.v4;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

enum TypeMapper {

    SIMPLE_CLASS(
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
            (type, context) -> typeVariableSetup((TypeVariable<?>) type, context));

    private final Predicate<Type> matching;
    private final BiFunction<Type, TypeSetup, TypeSetup> mapping;

    TypeMapper(final Predicate<Type> matching, final BiFunction<Type, TypeSetup, TypeSetup> mapping) {
        this.matching = matching;
        this.mapping = mapping;
    }

    private static TypeSetup typeVariableSetup(final TypeVariable<?> type, final TypeSetup context) {
        return context.getActualParameter(type.getName());
    }

    static TypeSetup map(final Class<?> type) {
        return map(type, null);
    }

    static TypeSetup map(final Type type, final TypeSetup context) {
        return Stream.of(values())
                     .filter(mapper -> mapper.matching.test(type))
                     .findAny()
                     .map(mapper -> mapper.mapping.apply(type, context))
                     .orElseThrow(() -> new IllegalArgumentException("Unknown type of Type: " + type));
    }

    private enum ClassMapper {

        CLASS(PlainClassSetup::new),
        ARRAY(PlainArraySetup::new);

        private final Function<? super Class<?>, ? extends TypeSetup> mapping;

        ClassMapper(final Function<? super Class<?>, ? extends TypeSetup> mapping) {
            this.mapping = mapping;
        }

        static TypeSetup map(final Class<?> underlyingClass) {
            return (underlyingClass.isArray() ? ARRAY : CLASS).mapping.apply(underlyingClass);
        }
    }
}
