package de.team33.libs.typing.v4;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * Represents the composition of a definite type that can be based on a generic as well as a non-generic class.
 */
public abstract class RawType {

    private static final String NOT_DECLARED_IN_THIS = "Member (%s) is not declared in the context of this Type (%s)";

    /**
     * Returns the primary {@link Class} on which this {@link RawType} is based.
     */
    public abstract Class<?> getPrimeClass();

    /**
     * Returns the formal type parameters of the (generic) type underlying this {@link RawType}.
     *
     * @see #getActualParameters()
     */
    public abstract List<String> getFormalParameters();

    /**
     * <p>Returns the actual parameters defining this {@link RawType}.</p>
     * <p>The result may be empty even if the {@linkplain #getFormalParameters() formal parameter list} is not.
     * Otherwise the formal and actual parameter list are of the same size and corresponding order.</p>
     *
     * @see #getFormalParameters()
     */
    public abstract List<RawType> getActualParameters();

    /**
     * Returns a specific actual parameter based on the corresponding formal parameter.
     *
     * @throws IllegalArgumentException when {@code <formalParameter>} is invalid.
     */
    public final RawType getActualParameter(final String formalParameter) throws IllegalArgumentException {
        final List<String> formalParameters = getFormalParameters();
        final List<RawType> actualParameters = getActualParameters();
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
     * Returns the {@link RawType} from which this {@link RawType} is derived (if so).
     *
     * @see Class#getSuperclass()
     * @see Class#getGenericSuperclass()
     */
    public final Optional<RawType> getSuperType() {
        return Optional.ofNullable(getPrimeClass().getGenericSuperclass())
                       .map(type -> TypeMapper.map(type, this::getActualParameter));
    }

    /**
     * Returns the interfaces as {@link RawType} from which this {@link RawType} is derived (if so).
     *
     * @see Class#getInterfaces()
     * @see Class#getGenericInterfaces()
     */
    public final Stream<RawType> getInterfaceTypes() {
        return Stream.of(getPrimeClass().getGenericInterfaces())
                     .map(type -> TypeMapper.map(type, this::getActualParameter));
    }

    /**
     * Returns all the {@link RawType}s (superclass, interfaces) from which this {@link RawType} is derived (if so).
     *
     * @see #getSuperType()
     * @see #getInterfaceTypes()
     */
    public final Stream<RawType> getSuperTypes() {
        return Stream.concat(
                getSuperType().map(Stream::of)
                              .orElseGet(Stream::empty),
                getInterfaceTypes());
    }

    /**
     * Returns the {@link RawType} of a given {@link Field} if it is defined in the hierarchy of this
     * {@link RawType}.
     *
     * @throws IllegalArgumentException if the given {@link Field} is not defined in the hierarchy of this
     *                                  {@link RawType}.
     *
     * @see Field#getType()
     * @see Field#getGenericType()
     */
    public final RawType typeOf(final Field field) {
        return Optional
                .ofNullable(nullableTypeOf(field, Field::getGenericType))
                .orElseThrow(() -> illegalMemberException(field));
    }

    /**
     * Returns the return {@link RawType} of a given {@link Method} if it is defined in the hierarchy of this
     * {@link RawType}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link RawType}.
     *
     * @see Method#getReturnType()
     * @see Method#getGenericReturnType()
     */
    public final RawType returnTypeOf(final Method method) {
        return Optional
                .ofNullable(nullableTypeOf(method, Method::getGenericReturnType))
                .orElseThrow(() -> illegalMemberException(method));
    }

    /**
     * Returns the parameter {@link RawType}s of a given {@link Method} if it is defined in the hierarchy of this
     * {@link RawType}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link RawType}.
     *
     * @see Method#getParameterTypes()
     * @see Method#getGenericParameterTypes()
     */
    public final List<RawType> parameterTypesOf(final Method method) {
        return Optional
                .ofNullable(nullableTypesOf(method, Method::getGenericParameterTypes))
                .orElseThrow(() -> illegalMemberException(method));
    }

    /**
     * Returns the exception {@link RawType}s of a given {@link Method} if it is defined in the hierarchy of this
     * {@link RawType}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link RawType}.
     */
    public final List<RawType> exceptionTypesOf(final Method method) {
        return Optional
                .ofNullable(nullableTypesOf(method, Method::getGenericExceptionTypes))
                .orElseThrow(() -> illegalMemberException(method));
    }

    private IllegalArgumentException illegalMemberException(final Member member) {
        return new IllegalArgumentException(String.format(NOT_DECLARED_IN_THIS, member, this));
    }

    private List<RawType> nullableTypesOf(final Method member,
                                          final Function<Method, Type[]> toGenericTypes) {
        if (getPrimeClass().equals(member.getDeclaringClass())) {
            return Stream.of(toGenericTypes.apply(member))
                         .map(type -> TypeMapper.map(type, this::getActualParameter))
                         .collect(Collectors.toList());
        } else {
            return getSuperTypes()
                    .map(st -> st.nullableTypesOf(member, toGenericTypes))
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(null);
        }
    }

    private <M extends Member> RawType nullableTypeOf(final M member,
                                                      final Function<M, Type> toGenericType) {
        if (getPrimeClass().equals(member.getDeclaringClass())) {
            return TypeMapper.map(toGenericType.apply(member), this::getActualParameter);
        } else {
            return getSuperTypes()
                    .map(st -> st.nullableTypeOf(member, toGenericType))
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(null);
        }
    }

    abstract Comparative comparative();

    @Override
    public final int hashCode() {
        return comparative().relativeHashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof RawType) && comparative().relativeEquals((RawType) obj));
    }

    @Override
    public abstract String toString();
}
