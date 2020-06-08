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
 * Represents the Shape of a definite type that can be based on a generic as well as a non-generic class.
 */
public abstract class Shape {

    private static final String NOT_DECLARED_IN_THIS = "Member (%s) is not declared in the context of this Shape (%s)";

    private final transient Lazy<List<Object>> listView =
            new Lazy<>(() -> Arrays.asList(getRawClass(), getActualParameters()));

    private final transient Lazy<Integer> hashView =
            new Lazy<>(() -> listView.get().hashCode());

    /**
     * Returns the raw {@link Class} on which this {@link Shape} is based.
     */
    public abstract Class<?> getRawClass();

    /**
     * Returns the formal type parameters of the (generic) type underlying this {@link Shape}.
     *
     * @see #getActualParameters()
     */
    public abstract List<String> getFormalParameters();

    /**
     * <p>Returns the actual parameters defining this {@link Shape}.</p>
     * <p>The result may be empty even if the {@linkplain #getFormalParameters() formal parameter list} is not.
     * Otherwise the formal and actual parameter list are of the same size and order.</p>
     *
     * @see #getFormalParameters()
     */
    public abstract List<Shape> getActualParameters();

    /**
     * Returns a specific actual parameter based on the corresponding formal parameter.
     *
     * @throws IllegalArgumentException when {@code <formalParameter>} is invalid.
     */
    public final Shape getActualParameter(final String formalParameter) throws IllegalArgumentException {
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
     * Returns the {@link Shape} from which this {@link Shape} is derived (if so).
     *
     * @see Class#getSuperclass()
     * @see Class#getGenericSuperclass()
     */
    public final Optional<Shape> getSuperShape() {
        return Optional.ofNullable(getRawClass().getGenericSuperclass())
                       .map(this::map);
    }

    /**
     * Returns the interfaces as {@link Shape} from which this {@link Shape} is derived (if so).
     *
     * @see Class#getInterfaces()
     * @see Class#getGenericInterfaces()
     */
    public final Stream<Shape> getInterfaces() {
        return Stream.of(getRawClass().getGenericInterfaces())
                     .map(this::map);
    }

    /**
     * Returns all the {@link Shape}s (superclass, interfaces) from which this {@link Shape} is derived (if so).
     *
     * @see #getSuperShape()
     * @see #getInterfaces()
     */
    public final Stream<Shape> getSuperShapes() {
        return Stream.concat(
                getSuperShape().map(Stream::of)
                               .orElseGet(Stream::empty),
                getInterfaces());
    }

    /**
     * Returns the {@link Shape} of a given {@link Field} if it is defined in the hierarchy of this {@link Shape}.
     *
     * @throws IllegalArgumentException if the given {@link Field} is not defined in the hierarchy of this
     *                                  {@link Shape}.
     *
     * @see Field#getType()
     * @see Field#getGenericType()
     */
    public final Shape shapeOf(final Field field) {
        return Optional
                .ofNullable(nullableShapeOf(field, Field::getGenericType))
                .orElseThrow(() -> illegalMemberException(field));
    }

    /**
     * Returns the return {@link Shape} of a given {@link Method} if it is defined in the hierarchy of this
     * {@link Shape}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link Shape}.
     *
     * @see Method#getReturnType()
     * @see Method#getGenericReturnType()
     */
    public final Shape returnShapeOf(final Method method) {
        return Optional
                .ofNullable(nullableShapeOf(method, Method::getGenericReturnType))
                .orElseThrow(() -> illegalMemberException(method));
    }

    /**
     * Returns the parameter {@link Shape}s of a given {@link Method} if it is defined in the hierarchy of this
     * {@link Shape}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link Shape}.
     *
     * @see Method#getParameterTypes()
     * @see Method#getGenericParameterTypes()
     */
    public final List<Shape> parameterShapesOf(final Method method) {
        return Optional
                .ofNullable(nullableShapesOf(method, Method::getGenericParameterTypes))
                .orElseThrow(() -> illegalMemberException(method));
    }

    /**
     * Returns the exception {@link Shape}s of a given {@link Method} if it is defined in the hierarchy of this
     * {@link Shape}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link Shape}.
     */
    public final List<Shape> exceptionShapesOf(final Method method) {
        return Optional
                .ofNullable(nullableShapesOf(method, Method::getGenericExceptionTypes))
                .orElseThrow(() -> illegalMemberException(method));
    }

    private IllegalArgumentException illegalMemberException(final Member member) {
        return new IllegalArgumentException(String.format(NOT_DECLARED_IN_THIS, member, this));
    }

    private List<Shape> nullableShapesOf(final Method member,
                                         final Function<Method, Type[]> toGenericTypes) {
        if (getRawClass().equals(member.getDeclaringClass())) {
            return Stream.of(toGenericTypes.apply(member))
                         .map(this::map)
                         .collect(Collectors.toList());
        } else {
            return getSuperShapes()
                    .map(st -> st.nullableShapesOf(member, toGenericTypes))
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(null);
        }
    }

    private <M extends Member> Shape nullableShapeOf(final M member,
                                                     final Function<M, Type> toGenericType) {
        if (getRawClass().equals(member.getDeclaringClass())) {
            return map(toGenericType.apply(member));
        } else {
            return getSuperShapes()
                    .map(st -> st.nullableShapeOf(member, toGenericType))
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(null);
        }
    }

    private Shape map(final Type type) {
        return TypeMapper.map(type, this);
    }

    @Override
    public final int hashCode() {
        return hashView.get();
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof Shape) && listView.get().equals(((Shape) obj).listView.get()));
    }

    @Override
    public abstract String toString();
}
