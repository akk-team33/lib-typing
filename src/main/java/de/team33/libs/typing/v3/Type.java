package de.team33.libs.typing.v3;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableList;

/**
 * <p>
 * Represents a fully defined type description, possibly based on a generic type.
 * </p><p>
 * For example, an instance of {@code Type<Map<String, List<String>>>}
 * represents the type {@code Map<String, List<String>>}.
 * </p><p>
 * To get an instance of (any) Type, you first need to create a fully defined derivative of Type.
 * Only this can be instantiated.
 * The easiest way to achieve this is to use an anonymous derivation with simultaneous instantiation. Example:
 * </p><pre>
 * final Type&lt;Map&lt;String, List&lt;String&gt;&gt;&gt;
 *         mapStringToStringListType = new Type&lt;Map&lt;String, List&lt;String&gt;&gt;&gt;() { };
 * </pre><p>
 * If a simple class object already fully defines the type in question, there is a convenience method to obtain an
 * instance of Type. Example:
 * </p><pre>
 * final Type&lt;String&gt;
 *         stringType = Type.of(String.class);
 * </pre><p>
 * <b>Note</b>: This class is defined as an abstract class (without abstract methods) to force a derivative for an
 * instantiation.
 * </p>
 */
@SuppressWarnings({
        "MethodMayBeStatic", "AbstractClassWithoutAbstractMethods", "unused", "StaticMethodOnlyUsedInOneClass"})
public abstract class Type<T> {

    private static final String SUPER_TYPE = "superType";
    private static final String SUPER_TYPES = "superTypes";

    private final Stage stage;
    private final LateBound lateBound = new LateBound();

    /**
     * Initializes a derivative of a type description based on its own full definition.
     */
    protected Type() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.stage = TypeVariant.toStage(genericSuperclass.getActualTypeArguments()[0], emptyMap());
    }

    private Type(final Stage stage) {
        this.stage = stage;
    }

    /**
     * A simple type description based on a simple, fully defined {@link Class}.
     *
     * @param underlyingClass the intended underlying {@link Class}.
     */
    public static <T> Type<T> of(final Class<T> underlyingClass) {
        return new Type<T>(ClassVariant.toStage(underlyingClass)) {
        };
    }

    private static Type<?> upon(final Stage stage) {
        return new Type(stage) {
        };
    }

    /**
     * The {@link Class} underlying this type description.
     */
    public final Class<?> getUnderlyingClass() {
        return stage.getUnderlyingClass();
    }

    /**
     * The formal type parameters that formally complement the
     * {@linkplain #getUnderlyingClass() underlying class} to a complete type description. The result is empty if the
     * underlying class itself already formally represents a complete type description.
     *
     * @see #getActualParameters()
     */
    public final List<String> getFormalParameters() {
        return lateBound.get("formalParameters", stage::getFormalParameters);
    }

    /**
     * The actual type parameters that complement the {@linkplain #getUnderlyingClass() underlying class} to a
     * complete type description. The result is empty if the underlying class itself already represents a complete
     * type description.
     *
     * @see #getFormalParameters()
     */
    public final List<Type<?>> getActualParameters() {
        return lateBound.get("actualParameters", this::newActualParameters);
    }

    private List<Type<?>> newActualParameters() {
        return unmodifiableList(
                stage.getActualParameters()
                        .map(Type::upon)
                        .collect(Collectors.toList())
        );
    }

    /**
     * A complete type description for the given {@code memberType}.
     *
     * @param memberType a {@link java.lang.reflect.Type} that exists in the context of this type description, e.g. the
     *                   {@linkplain Field#getGenericType() type of a field} or
     *                   {@linkplain Method#getGenericReturnType() result} or the
     *                   {@linkplain Method#getGenericParameterTypes() parameter of a method} of the underlying class.
     */
    public final Type<?> getMemberType(final java.lang.reflect.Type memberType) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * A complete type description for the super class of this type.
     */
    public final Type<?> getSuperType() {
        return lateBound.get(SUPER_TYPE, this::newSuperType);
    }

    private Type<?> newSuperType() {
        return getMemberType(getUnderlyingClass().getGenericSuperclass());
    }

    /**
     * A list of complete type descriptions for all the super interfaces of this type.
     */
    public final List<Type<?>> getSuperTypes() {
        return lateBound.get(SUPER_TYPES, this::newSuperTypes);
    }

    private List<Type<?>> newSuperTypes() {
        return unmodifiableList(
                Stream.of(getUnderlyingClass().getGenericInterfaces())
                        .map(this::getMemberType)
                        .collect(Collectors.toList())
        );
    }
}
