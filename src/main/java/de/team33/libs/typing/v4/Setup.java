package de.team33.libs.typing.v4;

import de.team33.libs.provision.v2.Lazy;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the setup of a definite type that can be based on a generic as well as a non-generic class.
 */
public abstract class Setup {

    private static final String NOT_DECLARED_IN_THIS = "Member (%s) is not declared in the context of this Setup (%s)";

    private final transient Lazy<List<Object>> listView =
            new Lazy<>(() -> Arrays.asList(getPrimeClass(), getActualParameters()));

    private final transient Lazy<Integer> hashView =
            new Lazy<>(() -> listView.get().hashCode());

    /**
     * Returns the primary {@link Class} on which this {@link Setup} is based.
     */
    public abstract Class<?> getPrimeClass();

    /**
     * Returns the formal type parameters of the (generic) type underlying this {@link Setup}.
     *
     * @see #getActualParameters()
     */
    public abstract List<String> getFormalParameters();

    /**
     * <p>Returns the actual parameters defining this {@link Setup}.</p>
     * <p>The result may be empty even if the {@linkplain #getFormalParameters() formal parameter list} is not.
     * Otherwise the formal and actual parameter list are of the same size and corresponding order.</p>
     *
     * @see #getFormalParameters()
     */
    public abstract List<Setup> getActualParameters();

    /**
     * Returns a specific actual parameter based on the corresponding formal parameter.
     *
     * @throws IllegalArgumentException when {@code <formalParameter>} is invalid.
     */
    public final Setup getActualParameter(final String formalParameter) throws IllegalArgumentException {
        final List<String> formalParameters = getFormalParameters();
        return Optional.of(formalParameters.indexOf(formalParameter))
                       .filter(index -> 0 <= index)
                       .map(index -> getActualParameters().get(index))
                       .orElseThrow(() -> new IllegalArgumentException(
                               String.format(
                                       "formal parameter <%s> not found in %s",
                                       formalParameter, formalParameters)));
    }

    /**
     * Returns the {@link Setup} from which this {@link Setup} is derived (if so).
     *
     * @see Class#getSuperclass()
     * @see Class#getGenericSuperclass()
     */
    public final Optional<Setup> getSuperModel() {
        return Optional.ofNullable(getPrimeClass().getGenericSuperclass())
                       .map(this::map);
    }

    /**
     * Returns the interfaces as {@link Setup} from which this {@link Setup} is derived (if so).
     *
     * @see Class#getInterfaces()
     * @see Class#getGenericInterfaces()
     */
    public final Stream<Setup> getInterfaceModels() {
        return Stream.of(getPrimeClass().getGenericInterfaces())
                     .map(this::map);
    }

    /**
     * Returns all the {@link Setup}s (superclass, interfaces) from which this {@link Setup} is derived (if so).
     *
     * @see #getSuperModel()
     * @see #getInterfaceModels()
     */
    public final Stream<Setup> getSuperModels() {
        return Stream.concat(
                getSuperModel().map(Stream::of)
                               .orElseGet(Stream::empty),
                getInterfaceModels());
    }

    /**
     * Returns the {@link Setup} of a given {@link Field} if it is defined in the hierarchy of this {@link Setup}.
     *
     * @throws IllegalArgumentException if the given {@link Field} is not defined in the hierarchy of this
     *                                  {@link Setup}.
     *
     * @see Field#getType()
     * @see Field#getGenericType()
     */
    public final Setup modelOf(final Field field) {
        return Optional
                .ofNullable(nullableModelOf(field, Field::getGenericType))
                .orElseThrow(() -> illegalMemberException(field));
    }

    /**
     * Returns the return {@link Setup} of a given {@link Method} if it is defined in the hierarchy of this
     * {@link Setup}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link Setup}.
     *
     * @see Method#getReturnType()
     * @see Method#getGenericReturnType()
     */
    public final Setup returnModelOf(final Method method) {
        return Optional
                .ofNullable(nullableModelOf(method, Method::getGenericReturnType))
                .orElseThrow(() -> illegalMemberException(method));
    }

    /**
     * Returns the parameter {@link Setup}s of a given {@link Method} if it is defined in the hierarchy of this
     * {@link Setup}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link Setup}.
     *
     * @see Method#getParameterTypes()
     * @see Method#getGenericParameterTypes()
     */
    public final List<Setup> parameterModelsOf(final Method method) {
        return Optional
                .ofNullable(nullableModelsOf(method, Method::getGenericParameterTypes))
                .orElseThrow(() -> illegalMemberException(method));
    }

    /**
     * Returns the exception {@link Setup}s of a given {@link Method} if it is defined in the hierarchy of this
     * {@link Setup}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link Setup}.
     */
    public final List<Setup> exceptionModelsOf(final Method method) {
        return Optional
                .ofNullable(nullableModelsOf(method, Method::getGenericExceptionTypes))
                .orElseThrow(() -> illegalMemberException(method));
    }

    private IllegalArgumentException illegalMemberException(final Member member) {
        return new IllegalArgumentException(String.format(NOT_DECLARED_IN_THIS, member, this));
    }

    private List<Setup> nullableModelsOf(final Method member,
                                         final Function<Method, Type[]> toGenericTypes) {
        if (getPrimeClass().equals(member.getDeclaringClass())) {
            return Stream.of(toGenericTypes.apply(member))
                         .map(this::map)
                         .collect(Collectors.toList());
        } else {
            return getSuperModels()
                    .map(st -> st.nullableModelsOf(member, toGenericTypes))
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(null);
        }
    }

    private <M extends Member> Setup nullableModelOf(final M member,
                                                     final Function<M, Type> toGenericType) {
        if (getPrimeClass().equals(member.getDeclaringClass())) {
            return map(toGenericType.apply(member));
        } else {
            return getSuperModels()
                    .map(st -> st.nullableModelOf(member, toGenericType))
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(null);
        }
    }

    private Setup map(final Type type) {
        return TypeMapper.map(type, this);
    }

    @Override
    public final int hashCode() {
        return hashView.get();
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof Setup) && listView.get().equals(((Setup) obj).listView.get()));
    }

    @Override
    public abstract String toString();
}
