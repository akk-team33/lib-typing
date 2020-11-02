package de.team33.libs.typing.v4;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

enum TypeMapper {

    SIMPLE_CLASS(
            type -> type instanceof Class<?>,
            (type, context) -> ClassMapper.map((Class<?>) type)),

    GENERIC_ARRAY(
            type -> type instanceof java.lang.reflect.GenericArrayType,
            ((type, context) -> new GenericArrayType((java.lang.reflect.GenericArrayType) type, context))),

    PARAMETERIZED_TYPE(
            type -> type instanceof java.lang.reflect.ParameterizedType,
            (type, context) -> new ParameterizedType((java.lang.reflect.ParameterizedType) type, context)),

    TYPE_VARIABLE(
            type -> type instanceof TypeVariable,
            (type, context) -> typeVariableType((TypeVariable<?>) type, context));

    private final Predicate<Type> matching;
    private final BiFunction<Type, Context, RawType> mapping;

    TypeMapper(final Predicate<Type> matching, final BiFunction<Type, Context, RawType> mapping) {
        this.matching = matching;
        this.mapping = mapping;
    }

    private static RawType typeVariableType(final TypeVariable<?> type, final Context context) {
        return context.getActual(type.getName());
    }

    static RawType map_(final Type type) {
        return map_(type, Context.NULL);
    }

    static RawType map_(final Type type, final Context context) {
        return Stream.of(values())
                     .filter(mapper -> mapper.matching.test(type))
                     .findAny()
                     .map(mapper -> mapper.mapping.apply(type, context))
                     .orElseThrow(() -> new IllegalArgumentException("Unknown type of Type: " + type));
    }

    private enum ClassMapper {

        CLASS(PlainClassType::new),
        ARRAY(PlainArrayType::new);

        private final Function<? super Class<?>, ? extends RawType> mapping;

        ClassMapper(final Function<? super Class<?>, ? extends RawType> mapping) {
            this.mapping = mapping;
        }

        static RawType map(final Class<?> underlyingClass) {
            return (underlyingClass.isArray() ? ARRAY : CLASS).mapping.apply(underlyingClass);
        }
    }
}
