package de.team33.libs.typing.v4;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the setup of a definite type that can be based on a generic as well as a non-generic class.
 */
public abstract class TypeSetup {

    private static final String NOT_DECLARED_IN_THIS = "Member (%s) is not declared in the context of this Setup (%s)";

    private final transient Lazy<Integer> hashView = new Lazy<>(() -> toList().hashCode());

    /**
     * Returns the primary {@link Class} on which this {@link TypeSetup} is based.
     */
    public abstract Class<?> getPrimeClass();

    /**
     * Returns the formal type parameters of the (generic) type underlying this {@link TypeSetup}.
     *
     * @see #getActualParameters()
     */
    public abstract List<String> getFormalParameters();

    /**
     * <p>Returns the actual parameters defining this {@link TypeSetup}.</p>
     * <p>The result may be empty even if the {@linkplain #getFormalParameters() formal parameter list} is not.
     * Otherwise the formal and actual parameter list are of the same size and corresponding order.</p>
     *
     * @see #getFormalParameters()
     */
    public abstract List<TypeSetup> getActualParameters();

    /**
     * Returns a specific actual parameter based on the corresponding formal parameter.
     *
     * @throws IllegalArgumentException when {@code <formalParameter>} is invalid.
     */
    public final TypeSetup getActualParameter(final String formalParameter) throws IllegalArgumentException {
        final List<String> formalParameters = getFormalParameters();
        final List<TypeSetup> actualParameters = getActualParameters();
        final int index = formalParameters.indexOf(formalParameter);
        if (0 > index) {
            throw new IllegalArgumentException(String.format(
                    "formal parameter <%s> not found in %s",
                    formalParameter, formalParameters));
        } else if (index >= actualParameters.size()) {
            throw new IllegalArgumentException(String.format(
                    "formal parameter [%d] (<%s> in %s) not found in actual parameters %s",
                    index, formalParameter, formalParameters, actualParameters));
        } else {
            return actualParameters.get(index);
        }
    }

    /**
     * Returns the {@link TypeSetup} from which this {@link TypeSetup} is derived (if so).
     *
     * @see Class#getSuperclass()
     * @see Class#getGenericSuperclass()
     */
    public final Optional<TypeSetup> getSuperSetup() {
        return Optional.ofNullable(getPrimeClass().getGenericSuperclass())
                       .map(this::map);
    }

    /**
     * Returns the interfaces as {@link TypeSetup} from which this {@link TypeSetup} is derived (if so).
     *
     * @see Class#getInterfaces()
     * @see Class#getGenericInterfaces()
     */
    public final Stream<TypeSetup> getInterfaceSetups() {
        return Stream.of(getPrimeClass().getGenericInterfaces())
                     .map(this::map);
    }

    /**
     * Returns all the {@link TypeSetup}s (superclass, interfaces) from which this {@link TypeSetup} is derived (if so).
     *
     * @see #getSuperSetup()
     * @see #getInterfaceSetups()
     */
    public final Stream<TypeSetup> getSuperSetups() {
        return Stream.concat(
                getSuperSetup().map(Stream::of)
                               .orElseGet(Stream::empty),
                getInterfaceSetups());
    }

    /**
     * Returns the {@link TypeSetup} of a given {@link Field} if it is defined in the hierarchy of this {@link TypeSetup}.
     *
     * @throws IllegalArgumentException if the given {@link Field} is not defined in the hierarchy of this
     *                                  {@link TypeSetup}.
     *
     * @see Field#getType()
     * @see Field#getGenericType()
     */
    public final TypeSetup setupOf(final Field field) {
        return Optional
                .ofNullable(nullableSetupOf(field, Field::getGenericType))
                .orElseThrow(() -> illegalMemberException(field));
    }

    /**
     * Returns the return {@link TypeSetup} of a given {@link Method} if it is defined in the hierarchy of this
     * {@link TypeSetup}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link TypeSetup}.
     *
     * @see Method#getReturnType()
     * @see Method#getGenericReturnType()
     */
    public final TypeSetup returnSetupOf(final Method method) {
        return Optional
                .ofNullable(nullableSetupOf(method, Method::getGenericReturnType))
                .orElseThrow(() -> illegalMemberException(method));
    }

    /**
     * Returns the parameter {@link TypeSetup}s of a given {@link Method} if it is defined in the hierarchy of this
     * {@link TypeSetup}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link TypeSetup}.
     *
     * @see Method#getParameterTypes()
     * @see Method#getGenericParameterTypes()
     */
    public final List<TypeSetup> parameterSetupsOf(final Method method) {
        return Optional
                .ofNullable(nullableSetupsOf(method, Method::getGenericParameterTypes))
                .orElseThrow(() -> illegalMemberException(method));
    }

    /**
     * Returns the exception {@link TypeSetup}s of a given {@link Method} if it is defined in the hierarchy of this
     * {@link TypeSetup}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link TypeSetup}.
     */
    public final List<TypeSetup> exceptionSetupsOf(final Method method) {
        return Optional
                .ofNullable(nullableSetupsOf(method, Method::getGenericExceptionTypes))
                .orElseThrow(() -> illegalMemberException(method));
    }

    private IllegalArgumentException illegalMemberException(final Member member) {
        return new IllegalArgumentException(String.format(NOT_DECLARED_IN_THIS, member, this));
    }

    private List<TypeSetup> nullableSetupsOf(final Method member,
                                             final Function<Method, Type[]> toGenericTypes) {
        if (getPrimeClass().equals(member.getDeclaringClass())) {
            return Stream.of(toGenericTypes.apply(member))
                         .map(this::map)
                         .collect(Collectors.toList());
        } else {
            return getSuperSetups()
                    .map(st -> st.nullableSetupsOf(member, toGenericTypes))
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(null);
        }
    }

    private <M extends Member> TypeSetup nullableSetupOf(final M member,
                                                         final Function<M, Type> toGenericType) {
        if (getPrimeClass().equals(member.getDeclaringClass())) {
            return map(toGenericType.apply(member));
        } else {
            return getSuperSetups()
                    .map(st -> st.nullableSetupOf(member, toGenericType))
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(null);
        }
    }

    private TypeSetup map(final Type type) {
        return TypeMapper.map(type, this);
    }

    @Override
    public final int hashCode() {
        return hashView.get();
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof TypeSetup) && toList().equals(((TypeSetup) obj).toList()));
    }

    @Override
    public abstract String toString();

    /**
     * Returns a list view of this {@link TypeSetup} consisting of two elements:
     * <ol>
     *     <li>the {@linkplain #getPrimeClass() prime class}</li>
     *     <li>the {@linkplain #getActualParameters() actual parameters}</li>
     * </ol>
     */
    abstract List<?> toList();
}
