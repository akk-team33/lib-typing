package de.team33.libs.typing.v2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a complete type description, possibly based on a generic class, mainly consisting of ...
 * <ul>
 * <li>... its underlying Class ({@link #getUnderlyingClass()})</li>
 * <li>... its actual type parameters ({@link #getActualParameters()})</li>
 * </ul>
 */
@SuppressWarnings("AbstractMethodWithMissingImplementations")
public abstract class TypeDesc {

    TypeDesc() {
    }

    private static Map<String, TypeDesc> newMap(final List<String> formal, final List<TypeDesc> actual) {
        final int size = actual.size();
        final Map<String, TypeDesc> result = new HashMap<>(size);
        for (int index = 0; index < size; ++index) {
            result.put(formal.get(index), actual.get(index));
        }
        return result;
    }

    /**
     * The {@link Class} underlying this type description.
     */
    public abstract Class<?> getUnderlyingClass();

    /**
     * The type parameters that complement the {@linkplain #getUnderlyingClass() underlying class} to a
     * complete type description. The result is empty if the underlying class itself already represents a complete
     * type description.
     *
     * @see #getFormalParameters()
     * @see #getActualParameters()
     */
    public final Map<String, TypeDesc> getParameters() {
        return newMap(getFormalParameters(), getActualParameters());
    }

    /**
     * The formal type parameters that formally complement the
     * {@linkplain #getUnderlyingClass() underlying class} to a complete type description. The result is empty if the
     * underlying class itself already formally represents a complete type description.
     *
     * @see #getParameters()
     * @see #getActualParameters()
     */
    public abstract List<String> getFormalParameters();

    /**
     * The actual type parameters that complement the {@linkplain #getUnderlyingClass() underlying class} to a
     * complete type description. The result is empty if the underlying class itself already represents a complete
     * type description.
     *
     * @see #getParameters()
     * @see #getFormalParameters()
     */
    public abstract List<TypeDesc> getActualParameters();

    /*
     * Converts a (possibly) generic {@link Type} that exists in the context of this type description into a TypeDesc.
     * For example, the type of a field or the type of a parameter or result of a method of this type.
     */

    /**
     * A complete type description for the given {@code memberType}.
     *
     * @param memberType a (possibly generic) {@link Type} that exists in the context of this type description,
     *                   e.g. the {@linkplain Field#getGenericType() type of a field} or
     *                   {@linkplain Method#getGenericReturnType() result} or
     *                   {@linkplain Method#getGenericParameterTypes() parameter of a method} of the underlying class.
     */
    public final TypeDesc toTypeDesc(final Type memberType) {
        return TypeType.toDesc(memberType, getParameters());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getUnderlyingClass(), getActualParameters());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Two instances of a type description are equal if they are {@linkplain #getUnderlyingClass() based} on the same class
     * and defined by the same {@linkplain #getActualParameters() actual parameters}.
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
    public abstract String toString();
}
