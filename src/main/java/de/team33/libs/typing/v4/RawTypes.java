package de.team33.libs.typing.v4;

import de.team33.libs.typing.v4.experimental3.Case;
import de.team33.libs.typing.v4.experimental3.Cases;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

enum RawTypes implements Case<TypeContext, RawType> {

    CLASS(Filter.CLASS),
    ARRAY_CLASS(Filter.ARRAY_CLASS, Method.ARRAY_CLASS, Method.PLAIN_CLASS),
    PARAMETERIZED(Filter.PARAMETERIZED, Method.PARAMETERIZED),
    GENERIC_ARRAY(Filter.GENERIC_ARRAY, Method.GENERIC_ARRAY),
    TYPE_VARIABLE(Filter.TYPE_VARIABLE, Method.TYPE_VARIABLE, Method.FAIL);

    private static final Cases<TypeContext, RawType> CASES = Cases
            .checkAll(CLASS, ARRAY_CLASS)
            .whenNot(CLASS).check(PARAMETERIZED)
            .whenNot(PARAMETERIZED).check(GENERIC_ARRAY)
            .whenNot(GENERIC_ARRAY).check(TYPE_VARIABLE)
            .build();

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
        return CASES.apply(new TypeContext(type, context));
    }

    private static RawType typeVariableType(final TypeVariable<?> type, final Context context) {
        return context.getActual(type.getName());
    }

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

    @SuppressWarnings("InnerClassFieldHidesOuterClassField")
    @FunctionalInterface
    private interface Filter extends Predicate<Type> {
        Filter CLASS = type -> type instanceof Class;
        Filter ARRAY_CLASS = type -> ((Class<?>) type).isArray();
        Filter PARAMETERIZED = type -> type instanceof java.lang.reflect.ParameterizedType;
        Filter GENERIC_ARRAY = type -> type instanceof java.lang.reflect.GenericArrayType;
        Filter TYPE_VARIABLE = type -> type instanceof TypeVariable;
    }

    @SuppressWarnings("InnerClassFieldHidesOuterClassField")
    @FunctionalInterface
    private interface Method extends Function<TypeContext, RawType> {
        Method ARRAY_CLASS = ctx -> new PlainArrayType((Class<?>) ctx.type);
        Method PLAIN_CLASS = ctx -> new PlainClassType((Class<?>) ctx.type);
        Method PARAMETERIZED = ctx -> new ParameterizedType((java.lang.reflect.ParameterizedType) ctx.type, ctx.context);
        Method GENERIC_ARRAY = ctx -> new GenericArrayType((java.lang.reflect.GenericArrayType) ctx.type, ctx.context);
        Method TYPE_VARIABLE = ctx -> typeVariableType((TypeVariable<?>) ctx.type, ctx.context);
        Method FAIL = ctx -> {
            throw new IllegalArgumentException("unknown type of type: " + ctx.type.getClass());
        };
    }
}
