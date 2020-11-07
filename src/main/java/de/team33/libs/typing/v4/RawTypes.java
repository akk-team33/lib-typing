package de.team33.libs.typing.v4;

import de.team33.libs.typing.v4.experimental3.Case;
import de.team33.libs.typing.v4.experimental3.Cases;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.team33.libs.typing.v4.experimental3.Case.*;

enum RawTypes implements Case<TypeContext, RawType> {

    CLASS(type -> type instanceof Class),

    ARRAY_CLASS(type -> ((Class<?>) type).isArray(),
                ctx -> new PlainArrayType((Class<?>) ctx.type),
                ctx -> new PlainClassType((Class<?>) ctx.type)),

    PARAMETERIZED(type -> type instanceof java.lang.reflect.ParameterizedType,
                  ctx -> new ParameterizedType((java.lang.reflect.ParameterizedType) ctx.type, ctx.context)),

    GENERIC_ARRAY(type -> type instanceof java.lang.reflect.GenericArrayType,
                  ctx -> new GenericArrayType((java.lang.reflect.GenericArrayType) ctx.type, ctx.context)),

    TYPE_VARIABLE(type -> type instanceof java.lang.reflect.TypeVariable,
                  ctx -> typeVariableType((TypeVariable<?>) ctx.type, ctx.context),
                  RawTypes::fail);

    private final Predicate<Type> predicate;
    private final Function<TypeContext, RawType> positive;
    private final Function<TypeContext, RawType> negative;

    RawTypes(final Predicate<Type> predicate) {
        this(predicate, null, null);
    }

    RawTypes(final Predicate<Type> predicate,
             final Function<TypeContext, RawType> positive) {
        this(predicate, positive, null);
    }

    RawTypes(final Predicate<Type> predicate,
             final Function<TypeContext, RawType> positive,
             final Function<TypeContext, RawType> negative) {
        this.predicate = predicate;
        this.positive = positive;
        this.negative = negative;
    }

    static RawType map(final Type type) {
        return map(type, Context.NULL);
    }

    static RawType map(final Type type, final Context context) {
        return choices.apply(new TypeContext(type, context));
    }

    private static RawType typeVariableType(final TypeVariable<?> type, final Context context) {
        return context.getActual(type.getName());
    }

    private static final Cases<TypeContext, RawType> choices = Cases
            .check(CLASS)
            .on(CLASS).check(ARRAY_CLASS)
            .on(not(CLASS)).check(PARAMETERIZED)
            .on(not(PARAMETERIZED)).check(GENERIC_ARRAY)
            .on(not(GENERIC_ARRAY)).check(TYPE_VARIABLE)
            .build();

    private static RawType fail(final TypeContext typeContext) {
        throw new IllegalArgumentException("unknown type of type: " + typeContext.type.getClass());
    }

    @Override
    public final boolean isMatching(final TypeContext input) {
        return predicate.test(input.type);
    }

    @Override
    public final Optional<Function<TypeContext, RawType>> getPositive() {
        return Optional.ofNullable(positive);
    }

    @Override
    public final Optional<Function<TypeContext, RawType>> getNegative() {
        return Optional.ofNullable(negative);
    }
}
