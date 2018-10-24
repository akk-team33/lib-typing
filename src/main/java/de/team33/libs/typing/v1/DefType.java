package de.team33.libs.typing.v1;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

/**
 * <p>
 * Represents a fully defined type, possibly based on a generic class.
 * </p><p>
 * For example, an instance of {@code DefType<Map<String, List<String>>>}
 * represents the type {@code Map<String, List<String>>}.
 * </p><p>
 * To get an instance of (any) DefType, you first need to create a fully defined derivative of DefType.
 * Only this can be instantiated.
 * The easiest way to achieve this is to use an anonymous derivation with simultaneous instantiation. Example:
 * </p><pre>
 * final DefType&lt;Map&lt;String, List&lt;String&gt;&gt;&gt; mapStringToStringListType
 *         = new DefType&lt;Map&lt;String, List&lt;String&gt;&gt;&gt;() { };
 * </pre><p>
 * If a simple class object already fully defines the type in question,
 * there is a convenience method to obtain an instance of DefType. Example:
 * </p><pre>
 * final DefType&lt;String&gt; stringType
 *         = DefType.of(String.class);
 * </pre><p>
 * <b>Note</b>: This class is defined as an abstract class, but does not define an abstract method
 * to enforce that a derivative is required for an instantiation.
 * </p>
 */
@SuppressWarnings({"AbstractClassWithoutAbstractMethods", "unused"})
public abstract class DefType<T> {

    @SuppressWarnings("rawtypes")
    private final Class<?> underlyingClass;
    @SuppressWarnings("rawtypes")
    private final ParameterMap parameters;

    private transient volatile String representation = null;

    /**
     * Initializes a {@link DefType} based on its own full definition
     */
    protected DefType() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        final Stage stage = TypeVariant.toStage(genericSuperclass.getActualTypeArguments()[0], ParameterMap.EMPTY);
        underlyingClass = stage.getUnderlyingClass();
        parameters = stage.getParameters();
    }

    DefType(final Stage stage) {
        underlyingClass = stage.getUnderlyingClass();
        parameters = stage.getParameters();
    }

    /**
     * Returns a {@link DefType} based on a simple, fully defined {@link Class}.
     */
    public static <T> DefType<T> of(final Class<T> simpleClass) {
        return new DefType<T>(new ClassStage(simpleClass)) {
        };
    }

    /**
     * Returns the {@link Class} on which this DefType is based.
     */
    public final Class<?> getUnderlyingClass() {
        return underlyingClass;
    }

    /**
     * Returns the type parameters defining this DefType.
     *
     * @see #getFormalParameters()
     * @see #getActualParameters()
     */
    public final Map<String, DefType<?>> getParameters() {
        // noinspection AssignmentOrReturnOfFieldWithMutableType
        return parameters;
    }

    /**
     * Returns the formal type parameter of the generic type underlying this DefType.
     *
     * @see #getParameters()
     * @see #getActualParameters()
     */
    public final List<String> getFormalParameters() {
        return parameters.getFormal();
    }

    /**
     * Returns the actual type parameters defining this DefType.
     *
     * @see #getParameters()
     * @see #getFormalParameters()
     */
    public final List<DefType<?>> getActualParameters() {
        return parameters.getActual();
    }

    /**
     * Converts a (possibly) generic {@link Type} that exists in the context of this DefType into a DefType.
     * For example, the type of a field or the type of a parameter or result of a method of this type.
     */
    public final DefType<?> getMemberType(final Type type) {
        return new DefType(TypeVariant.toStage(type, parameters)) {
        };
    }

    @Override
    public final int hashCode() {
        return Objects.hash(underlyingClass, parameters);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Two instances of DefType are equal if they are {@linkplain #getUnderlyingClass() based} on the same class
     * and defined by the same {@linkplain #getParameters() parameters}.
     * </p>
     */
    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof DefType) && isEqual((DefType<?>) obj));
    }

    private boolean isEqual(final DefType<?> other) {
        return underlyingClass.equals(other.underlyingClass) && parameters.equals(other.parameters);
    }

    @Override
    public final String toString() {
        return Optional.ofNullable(representation).orElseGet(() -> {
            final List<DefType<?>> actual = parameters.getActual();
            representation = underlyingClass.getSimpleName() + (
                    actual.isEmpty() ? "" : actual.stream()
                            .map(DefType::toString)
                            .collect(joining(", ", "<", ">")));
            return representation;
        });
    }

}
