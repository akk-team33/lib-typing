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
 * Represents the Model of a definite type that can be based on a generic as well as a non-generic class.
 */
public abstract class Model {

    private static final String NOT_DECLARED_IN_THIS = "Member (%s) is not declared in the context of this Model (%s)";

    private final transient Lazy<List<Object>> listView =
            new Lazy<>(() -> Arrays.asList(getRawClass(), getActualParameters()));

    private final transient Lazy<Integer> hashView =
            new Lazy<>(() -> listView.get().hashCode());

    /**
     * Returns the raw {@link Class} on which this {@link Model} is based.
     */
    public abstract Class<?> getRawClass();

    /**
     * Returns the formal type parameters of the (generic) type underlying this {@link Model}.
     *
     * @see #getActualParameters()
     */
    public abstract List<String> getFormalParameters();

    /**
     * <p>Returns the actual parameters defining this {@link Model}.</p>
     * <p>The result may be empty even if the {@linkplain #getFormalParameters() formal parameter list} is not.
     * Otherwise the formal and actual parameter list are of the same size and order.</p>
     *
     * @see #getFormalParameters()
     */
    public abstract List<Model> getActualParameters();

    /**
     * Returns a specific actual parameter based on the corresponding formal parameter.
     *
     * @throws IllegalArgumentException when {@code <formalParameter>} is invalid.
     */
    public final Model getActualParameter(final String formalParameter) throws IllegalArgumentException {
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
     * Returns the {@link Model} from which this {@link Model} is derived (if so).
     *
     * @see Class#getSuperclass()
     * @see Class#getGenericSuperclass()
     */
    public final Optional<Model> getSuperModel() {
        return Optional.ofNullable(getRawClass().getGenericSuperclass())
                       .map(this::map);
    }

    /**
     * Returns the interfaces as {@link Model} from which this {@link Model} is derived (if so).
     *
     * @see Class#getInterfaces()
     * @see Class#getGenericInterfaces()
     */
    public final Stream<Model> getInterfaceModels() {
        return Stream.of(getRawClass().getGenericInterfaces())
                     .map(this::map);
    }

    /**
     * Returns all the {@link Model}s (superclass, interfaces) from which this {@link Model} is derived (if so).
     *
     * @see #getSuperModel()
     * @see #getInterfaceModels()
     */
    public final Stream<Model> getSuperModels() {
        return Stream.concat(
                getSuperModel().map(Stream::of)
                               .orElseGet(Stream::empty),
                getInterfaceModels());
    }

    /**
     * Returns the {@link Model} of a given {@link Field} if it is defined in the hierarchy of this {@link Model}.
     *
     * @throws IllegalArgumentException if the given {@link Field} is not defined in the hierarchy of this
     *                                  {@link Model}.
     *
     * @see Field#getType()
     * @see Field#getGenericType()
     */
    public final Model modelOf(final Field field) {
        return Optional
                .ofNullable(nullableModelOf(field, Field::getGenericType))
                .orElseThrow(() -> illegalMemberException(field));
    }

    /**
     * Returns the return {@link Model} of a given {@link Method} if it is defined in the hierarchy of this
     * {@link Model}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link Model}.
     *
     * @see Method#getReturnType()
     * @see Method#getGenericReturnType()
     */
    public final Model returnModelOf(final Method method) {
        return Optional
                .ofNullable(nullableModelOf(method, Method::getGenericReturnType))
                .orElseThrow(() -> illegalMemberException(method));
    }

    /**
     * Returns the parameter {@link Model}s of a given {@link Method} if it is defined in the hierarchy of this
     * {@link Model}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link Model}.
     *
     * @see Method#getParameterTypes()
     * @see Method#getGenericParameterTypes()
     */
    public final List<Model> parameterModelsOf(final Method method) {
        return Optional
                .ofNullable(nullableModelsOf(method, Method::getGenericParameterTypes))
                .orElseThrow(() -> illegalMemberException(method));
    }

    /**
     * Returns the exception {@link Model}s of a given {@link Method} if it is defined in the hierarchy of this
     * {@link Model}.
     *
     * @throws IllegalArgumentException if the given {@link Method} is not defined in the hierarchy of this
     *                                  {@link Model}.
     */
    public final List<Model> exceptionModelsOf(final Method method) {
        return Optional
                .ofNullable(nullableModelsOf(method, Method::getGenericExceptionTypes))
                .orElseThrow(() -> illegalMemberException(method));
    }

    private IllegalArgumentException illegalMemberException(final Member member) {
        return new IllegalArgumentException(String.format(NOT_DECLARED_IN_THIS, member, this));
    }

    private List<Model> nullableModelsOf(final Method member,
                                         final Function<Method, Type[]> toGenericTypes) {
        if (getRawClass().equals(member.getDeclaringClass())) {
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

    private <M extends Member> Model nullableModelOf(final M member,
                                                     final Function<M, Type> toGenericType) {
        if (getRawClass().equals(member.getDeclaringClass())) {
            return map(toGenericType.apply(member));
        } else {
            return getSuperModels()
                    .map(st -> st.nullableModelOf(member, toGenericType))
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(null);
        }
    }

    private Model map(final Type type) {
        return TypeMapper.map(type, this);
    }

    @Override
    public final int hashCode() {
        return hashView.get();
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof Model) && listView.get().equals(((Model) obj).listView.get()));
    }

    @Override
    public abstract String toString();
}
