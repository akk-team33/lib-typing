package de.team33.libs.typing.v2;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

/**
 * Represents a type description, possibly based on a generic class, mainly consisting of ...
 * <ul>
 *     <li>... its underlying Class ({@link #getUnderlyingClass()})</li>
 *     <li>... its actual type parameters ({@link #getActualParameters()})</li>
 * </ul>
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
    public final Map<String, TypeDesc> getParameters() {
        return newMap(getFormalParameters(), getActualParameters());
    }

    private static Map<String, TypeDesc> newMap(final List<String> formal, final List<TypeDesc> actual) {
        final int size = formal.size();
        final Map<String, TypeDesc> result = new HashMap<>(size);
        for (int index = 0; index < size; ++index) {
            result.put(formal.get(index), actual.get(index));
        }
        return result;
    }

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
    public final TypeDesc getMemberType(final Type type) {
        return TypeType.toDesc(type, getParameters());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getUnderlyingClass(), getActualParameters());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Two instances of TypeDesc are equal if they are {@linkplain #getUnderlyingClass() based} on the same class
     * and defined by the same {@linkplain #getActualParameters() parameters}.
     * </p>
     */
    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof TypeDesc) && isEqual((TypeDesc) obj));
    }

    private boolean isEqual(final TypeDesc other) {
        return getUnderlyingClass().equals(other.getUnderlyingClass())
                && getActualParameters().equals(other.getActualParameters());
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
