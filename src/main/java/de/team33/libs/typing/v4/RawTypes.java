package de.team33.libs.typing.v4;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.function.Function;
import java.util.function.Predicate;

enum RawTypes implements Function<TypeContext, RawType> {

    CLASS(on(Filter.ARRAY_CLASS, Method.ARRAY_CLASS, Method.PLAIN_CLASS)),
    GENERIC_2(on(Filter.TYPE_VARIABLE, Method.TYPE_VARIABLE, Method.FAIL)),
    GENERIC_1(on(Filter.GENERIC_ARRAY, Method.GENERIC_ARRAY, GENERIC_2)),
    GENERIC(on(Filter.PARAMETERIZED, Method.PARAMETERIZED, GENERIC_1)),
    UNKNOWN(on(Filter.CLASS, CLASS, GENERIC));

    private final Function<TypeContext, RawType> backing;

    RawTypes(final Function<TypeContext, RawType> backing) {
        this.backing = backing;
    }

    static RawType map(final Type type) {
        return map(type, Context.NULL);
    }

    static RawType map(final Type type, final Context context) {
        return map(new TypeContext(type, context));
    }

    private static RawType map(final TypeContext typeContext) {
        return UNKNOWN.apply(typeContext);
    }

    private static Function<TypeContext, RawType> on(final Predicate<Type> condition,
                                                     final Function<TypeContext, RawType> positive,
                                                     final Function<TypeContext, RawType> negative) {
        return ctx -> condition.test(ctx.type) ? positive.apply(ctx) : negative.apply(ctx);
    }

    private static RawType typeVariableType(final TypeVariable<?> type, final Context context) {
        return context.getActual(type.getName());
    }

    @Override
    public final RawType apply(final TypeContext typeContext) {
        return backing.apply(typeContext);
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
