package de.team33.libs.typing.v2;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

/**
 * <p>
 * Represents a type description, possibly based on a generic class.
 * </p><p>
 * For example, an instance of {@code TypeDesc<Map<String, List<String>>>}
 * represents the type {@code Map<String, List<String>>}.
 * </p><p>
 * To get an instance of (any) TypeDesc, you first need to create a fully defined derivative of TypeDesc.
 * Only this can be instantiated.
 * The easiest way to achieve this is to use an anonymous derivation with simultaneous instantiation. Example:
 * </p><pre>
 * final TypeDesc&lt;Map&lt;String, List&lt;String&gt;&gt;&gt; mapStringToStringListType
 *         = new TypeDesc&lt;Map&lt;String, List&lt;String&gt;&gt;&gt;() { };
 * </pre><p>
 * If a simple class object already fully defines the type in question,
 * there is a convenience method to obtain an instance of TypeDesc. Example:
 * </p><pre>
 * final TypeDesc&lt;String&gt; stringType
 *         = TypeDesc.of(String.class);
 * </pre><p>
 * <b>Note</b>: This class is defined as an abstract class, but does not define an abstract method
 * to enforce that a derivative is required for an instantiation.
 * </p>
 */
@SuppressWarnings("AbstractMethodWithMissingImplementations")
public abstract class TypeDesc {

    /**
     * Returns the {@link Class} this type description is based on.
     */
    public abstract Class<?> getUnderlyingClass();

    /**
     * Returns the type parameters defining this type description.
     *
     * @see #getFormalParameters()
     * @see #getActualParameters()
     */
    public abstract Map<String, TypeDesc> getParameters();

    /**
     * Returns the formal type parameter of the generic type underlying this type description.
     *
     * @see #getParameters()
     * @see #getActualParameters()
     */
    public abstract List<String> getFormalParameters();

    /**
     * Returns the actual type parameters defining this type description.
     *
     * @see #getParameters()
     * @see #getFormalParameters()
     */
    public abstract List<TypeDesc> getActualParameters();

    /**
     * Converts a (possibly) generic {@link Type} that exists in the context of this type description into a TypeDesc.
     * For example, the type of a field or the type of a parameter or result of a method of this type.
     */
    public abstract TypeDesc getMemberType(final Type type);

    @Override
    public final int hashCode() {
        return Objects.hash(getUnderlyingClass(), getParameters());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Two instances of TypeDesc are equal if they are {@linkplain #getUnderlyingClass() based} on the same class
     * and defined by the same {@linkplain #getParameters() parameters}.
     * </p>
     */
    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof TypeDesc) && isEqual((TypeDesc) obj));
    }

    private boolean isEqual(final TypeDesc other) {
        return getUnderlyingClass().equals(other.getUnderlyingClass()) && getParameters().equals(other.getParameters());
    }

    @Override
    public final String toString() {
        final List<TypeDesc> actual = getActualParameters();
        return getUnderlyingClass().getSimpleName() + (
                actual.isEmpty() ? "" : actual.stream()
                        .map(TypeDesc::toString)
                        .collect(joining(", ", "<", ">")));
    }
}
