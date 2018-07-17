package net.team33.typing;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class Generic<T> {

    @SuppressWarnings("rawtypes")
    private final Class<?> rawClass;
    @SuppressWarnings("rawtypes")
    private final Parameters parameters;

    private transient volatile String representation = null;

    protected Generic() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        final Variant variant = Variant.of(genericSuperclass.getActualTypeArguments()[0], Parameters.EMPTY);
        rawClass = variant.getRawClass();
        parameters = variant.getParameters();
    }

    Generic(final Variant variant) {
        rawClass = variant.getRawClass();
        parameters = variant.getParameters();
    }

    private Generic(final Class<T> simpleClass) {
        rawClass = simpleClass;
        parameters = Parameters.EMPTY;
    }

    public static <T> Generic<T> of(final Class<T> simpleClass) {
        return new Generic<T>(simpleClass) {
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

    public final Generic<?> getMemberType(final Type type) {
        return new Generic(Variant.of(type, parameters)) {
        };
    }

    @Override
    public final int hashCode() {
        return Objects.hash(rawClass, parameters);
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof Generic) && isEqual((Generic<?>) obj));
    }

    private boolean isEqual(final Generic<?> other) {
        return rawClass.equals(other.rawClass) && parameters.equals(other.parameters);
    }

    @Override
    public final String toString() {
        return Optional.ofNullable(representation).orElseGet(() -> {
            final List<Generic<?>> actual = parameters.getActual();
            representation = rawClass.getSimpleName() + (
                    actual.isEmpty() ? "" : actual.stream()
                            .map(Generic::toString)
                            .collect(joining(", ", "<", ">")));
            return representation;
        });
    }
}
