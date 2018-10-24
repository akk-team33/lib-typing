package de.team33.libs.typing.v1;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
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

    private final Stage stage;
    private transient volatile String representation = null;

    /**
     * Initializes a {@link DefType} based on its own full definition
     */
    protected DefType() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.stage = TypeVariant.toStage(
                genericSuperclass.getActualTypeArguments()[0],
                ClassVariant.toStage(getClass())
        );
    }

    DefType(final Stage stage) {
        this.stage = stage;
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
        return stage.getUnderlyingClass();
    }

    /**
     * Returns the formal type parameter of the generic type underlying this DefType.
     *
     * @see #getActualParameters()
     */
    public final List<String> getFormalParameters() {
        return stage.getFormalParameters();
    }

    /**
     * Returns the actual type parameters defining this DefType.
     *
     * @see #getFormalParameters()
     */
    public final List<DefType<?>> getActualParameters() {
        return stage.getActualParameters();
    }

    /**
     * Converts a (possibly) generic {@link Type} that exists in the context of this DefType into a DefType.
     * For example, the type of a field or the type of a parameter or result of a method of this type.
     */
    public final DefType<?> getMemberType(final Type type) {
        return new DefType(TypeVariant.toStage(type, stage)) {
        };
    }

    @Override
    public final int hashCode() {
        return Objects.hash(stage.getUnderlyingClass(), stage.getActualParameters());
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
        return getUnderlyingClass().equals(other.getUnderlyingClass())
                && getActualParameters().equals(other.getActualParameters());
    }

    @Override
    public final String toString() {
        return Optional.ofNullable(representation).orElseGet(() -> {
            final List<DefType<?>> actual = getActualParameters();
            representation = getUnderlyingClass().getSimpleName() + (
                    actual.isEmpty() ? "" : actual.stream()
                            .map(DefType::toString)
                            .collect(joining(", ", "<", ">")));
            return representation;
        });
    }

}
