package de.team33.libs.typing.v4;

import de.team33.libs.typing.v4.experimental4.Case;
import de.team33.libs.typing.v4.experimental4.Cases;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.team33.libs.typing.v4.experimental4.Case.not;

enum RawTypes implements Case<TypeContext, RawType> {

    CLASS(Case.none(), Filter.CLASS),
    ARRAY_CLASS(CLASS, Filter.ARRAY_CLASS, Method.ARRAY_CLASS),
    PLAIN_CLASS(not(ARRAY_CLASS), Filter.TRUE, Method.PLAIN_CLASS),
    PARAMETERIZED(not(CLASS), Filter.PARAMETERIZED, Method.PARAMETERIZED),
    GENERIC_ARRAY(not(PARAMETERIZED), Filter.GENERIC_ARRAY, Method.GENERIC_ARRAY),
    TYPE_VARIABLE(not(GENERIC_ARRAY), Filter.TYPE_VARIABLE, Method.TYPE_VARIABLE),
    FAIL(not(TYPE_VARIABLE), Filter.TRUE, Method.FAIL);

    private static final Cases<TypeContext, RawType> CASES = Cases.build(values());

    private final Case<TypeContext, RawType> preCondition;
    private final Predicate<Type> predicate;
    private final Function<TypeContext, RawType> method;

    RawTypes(final Case<TypeContext, RawType> preCondition, final Predicate<Type> predicate) {
        this(preCondition, predicate, null);
    }

    RawTypes(final Case<TypeContext, RawType> preCondition,
             final Predicate<Type> predicate,
             final Function<TypeContext, RawType> method) {
        this.preCondition = preCondition;
        this.predicate = predicate;
        this.method = method;
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

    @Override
    public final Case<TypeContext, RawType> getPreCondition() {
        return preCondition;
    }

    @Override
    public final boolean isDefault() {
        return Filter.TRUE == predicate;
    }

    @Override
    public final boolean isMatching(final TypeContext input) {
        return predicate.test(input.type);
    }

    @Override
    public final Optional<Function<TypeContext, RawType>> getMethod() {
        return Optional.ofNullable(method);
    }

    @SuppressWarnings("InnerClassFieldHidesOuterClassField")
    @FunctionalInterface
    private interface Filter extends Predicate<Type> {
        Filter TRUE = type -> true;
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
