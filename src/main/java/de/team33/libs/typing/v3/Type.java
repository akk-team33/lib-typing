package de.team33.libs.typing.v3;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Represents a complete type description, possibly based on a generic class.
 * </p><p>
 * For example, an instance of {@code Type<Map<String, List<String>>>}
 * represents the type {@code Map<String, List<String>>}.
 * </p><p>
 * To get an instance of Type, you first need to create a fully defined derivative of Type.
 * The easiest way to achieve this is to use an anonymous derivation with simultaneous instantiation. Example:
 * </p><pre>
 * final Type&lt;Map&lt;String, List&lt;String&gt;&gt;&gt; mapStringToStringListType
 *         = new Type&lt;Map&lt;String, List&lt;String&gt;&gt;&gt;() { };
 * </pre><p>
 * If a simple class object already fully defines the type in question,
 * there is a convenience method to obtain an instance of Type. Example:
 * </p><pre>
 * final Type&lt;String&gt; stringType
 *         = Type.of(String.class);
 * </pre><p>
 * <b>Note</b>: This class is defined as an abstract class (without defining an abstract method)
 * to enforce that a derivative is required for an instantiation.
 * </p>
 */
@SuppressWarnings({"AbstractClassWithoutAbstractMethods", "unused"})
public abstract class Type<T> {

    private static final String TO_STRING = "toString";

    private final Stage stage;
    private final LateBound late = new LateBound();

    /**
     * Initializes a {@link Type} based on its own full definition
     */
    protected Type() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.stage = TypeVariant.toStage(
                genericSuperclass.getActualTypeArguments()[0],
                ClassVariant.toStage(getClass())
        );
    }

    Type(final Stage stage) {
        this.stage = stage;
    }

    /**
     * Returns a {@link Type} based on a simple, fully defined {@link Class}.
     */
    public static <T> Type<T> of(final Class<T> simpleClass) {
        return new Type<T>(new ClassStage(simpleClass)) {
        };
    }

    /**
     * Returns the {@link Class} on which this Type is based.
     */
    public final Class<?> getUnderlyingClass() {
        return stage.getUnderlyingClass();
    }

    /**
     * Returns the formal type parameter of the generic type underlying this Type.
     *
     * @see #getActualParameters()
     */
    public final List<String> getFormalParameters() {
        return stage.getFormalParameters();
    }

    /**
     * Returns the actual type parameters defining this Type.
     *
     * @see #getFormalParameters()
     */
    public final List<Type<?>> getActualParameters() {
        return stage.getActualParameters();
    }

    /**
     * Converts a (possibly) generic {@link java.lang.reflect.Type} that exists in the context of this Type into a Type.
     * For example, the type of a field or the type of a parameter or result of a method of this type.
     */
    public final Type<?> getMemberType(final java.lang.reflect.Type type) {
        return new Type(TypeVariant.toStage(type, stage)) {
        };
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getUnderlyingClass(), getActualParameters());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Two instances of Type are equal if they are {@linkplain #getUnderlyingClass() based} on the same class
     * and defined by the same {@linkplain #getActualParameters() actual parameters}.
     * </p>
     */
    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof Type) && isEqual((Type<?>) obj));
    }

    private boolean isEqual(final Type<?> other) {
        return getUnderlyingClass().equals(other.getUnderlyingClass())
                && getActualParameters().equals(other.getActualParameters());
    }

    @Override
    public final String toString() {
        return late.get(TO_STRING, stage::toString);
    }
}
