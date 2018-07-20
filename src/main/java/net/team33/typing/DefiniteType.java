package net.team33.typing;

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
 * For example, an instance of {@code DefiniteType<Map<String, List<String>>>}
 * represents the type {@code Map<String, List<String>>}.
 * </p><p>
 * To get an instance of (any) DefiniteType, you first need to create a fully defined derivative of DefiniteType.
 * Only this can be instantiated directly.
 * The easiest way to achieve this is to use an anonymous derivation with simultaneous instantiation. Example:
 * </p><pre>
 * final DefiniteType&lt;Map&lt;String, List&lt;String&gt;&gt;&gt; stringToStringListMapType
 *         = new DefiniteType&lt;Map&lt;String, List&lt;String&gt;&gt;&gt;() { };
 * </pre><p>
 * If a simple class object already fully defines the type in question,
 * there is a convenience method to obtain an instance of DefiniteType. Example:
 * </p><pre>
 * final DefiniteType&lt;String&gt; stringType
 *         = DefiniteType.of(String.class);
 * </pre><p>
 * <b>Note</b>: This class is defined as an abstract class, but does not define an abstract method
 * to enforce that a derivative is required for an instantiation.
 * </p>
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class DefiniteType<T> {

    @SuppressWarnings("rawtypes")
    private final Class<?> rawClass;
    @SuppressWarnings("rawtypes")
    private final Parameters parameters;

    private transient volatile String representation = null;

    private void eg() {
        final DefiniteType<String> stringType
                = DefiniteType.of(String.class);
    }

    /**
     * @see DefiniteType
     */
    protected DefiniteType() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        final Variant variant = Variant.of(genericSuperclass.getActualTypeArguments()[0], Parameters.EMPTY);
        rawClass = variant.getRawClass();
        parameters = variant.getParameters();
    }

    DefiniteType(final Variant variant) {
        rawClass = variant.getRawClass();
        parameters = variant.getParameters();
    }

    private DefiniteType(final Class<T> simpleClass) {
        rawClass = simpleClass;
        parameters = Parameters.EMPTY;
    }

    /**
     * @see DefiniteType
     */
    public static <T> DefiniteType<T> of(final Class<T> simpleClass) {
        return new DefiniteType<T>(simpleClass) {
        };
    }

    @SuppressWarnings("rawtypes")
    public final Class<?> getRawClass() {
        return rawClass;
    }

    @SuppressWarnings("rawtypes")
    public final Parameters getParameters() {
        return parameters;
    }

    public final DefiniteType<?> getMemberType(final Type type) {
        return new DefiniteType(Variant.of(type, parameters)) {
        };
    }

    @Override
    public final int hashCode() {
        return Objects.hash(rawClass, parameters);
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof DefiniteType) && isEqual((DefiniteType<?>) obj));
    }

    private boolean isEqual(final DefiniteType<?> other) {
        return rawClass.equals(other.rawClass) && parameters.equals(other.parameters);
    }

    @Override
    public final String toString() {
        return Optional.ofNullable(representation).orElseGet(() -> {
            final List<DefiniteType<?>> actual = parameters.getActual();
            representation = rawClass.getSimpleName() + (
                    actual.isEmpty() ? "" : actual.stream()
                            .map(DefiniteType::toString)
                            .collect(joining(", ", "<", ">")));
            return representation;
        });
    }
}
