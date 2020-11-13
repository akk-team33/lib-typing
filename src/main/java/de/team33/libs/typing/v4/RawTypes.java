package de.team33.libs.typing.v4;

import de.team33.libs.typing.v4.experimental4.Case;
import de.team33.libs.typing.v4.experimental4.Cases;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.team33.libs.typing.v4.experimental4.Case.not;

enum RawTypes implements Case<TypeContext, Function<TypeContext, RawType>> {

    CLASS(Case.none(), Filter.CLASS),
    ARRAY_CLASS(CLASS, Filter.ARRAY_CLASS, Method.ARRAY_CLASS),
    PLAIN_CLASS(not(ARRAY_CLASS), null, Method.PLAIN_CLASS),
    PARAMETERIZED(not(CLASS), Filter.PARAMETERIZED, Method.PARAMETERIZED),
    GENERIC_ARRAY(not(PARAMETERIZED), Filter.GENERIC_ARRAY, Method.GENERIC_ARRAY),
    TYPE_VARIABLE(not(GENERIC_ARRAY), Filter.TYPE_VARIABLE, Method.TYPE_VARIABLE),
    FAIL(not(TYPE_VARIABLE), null, Method.FAIL);

    private static final Cases<TypeContext, Function<TypeContext, RawType>> CASES = Cases.build(values());

    private final Case<TypeContext, Function<TypeContext, RawType>> preCondition;
    private final Predicate<TypeContext> predicate;
    private final Function<TypeContext, RawType> method;

    RawTypes(final Case<TypeContext, Function<TypeContext, RawType>> preCondition, final Predicate<Type> predicate) {
        this(preCondition, predicate, null);
    }

    RawTypes(final Case<TypeContext, Function<TypeContext, RawType>> preCondition,
             final Predicate<Type> predicate,
             final Function<TypeContext, RawType> method) {
        this.preCondition = preCondition;
        this.predicate = (null == predicate) ? null : ctx -> predicate.test(ctx.type);
        this.method = method;
    }

    static RawType map(final Type type) {
        return map(type, Context.NULL);
    }

    static RawType map(final Type type, final Context context) {
        return map(new TypeContext(type, context));
    }

    private static RawType map(final TypeContext typeContext) {
        return CASES.apply(typeContext)
                    .apply(typeContext);
    }

    private static RawType typeVariableType(final TypeVariable<?> type, final Context context) {
        return context.getActual(type.getName());
    }

    @Override
    public final Case<TypeContext, Function<TypeContext, RawType>> getPreCondition() {
        return preCondition;
    }

    @Override
    public Optional<Predicate<TypeContext>> getCondition() {
        return Optional.ofNullable(predicate);
    }

    @Override
    public Optional<Function<TypeContext, RawType>> getResult() {
        return Optional.ofNullable(method);
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
