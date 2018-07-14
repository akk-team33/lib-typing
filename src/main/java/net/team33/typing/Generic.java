package net.team33.typing;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

@SuppressWarnings({
        "MethodMayBeStatic",
        "ClassNamePrefixedWithPackageName",
        "AbstractClassWithOnlyOneDirectInheritor",
        "AbstractClassWithoutAbstractMethods"})
public abstract class Generic<T> {

    @SuppressWarnings("rawtypes")
    private final Class<?> rawClass;
    @SuppressWarnings("rawtypes")
    private final Map<String, Generic<?>> parameters;

    private transient volatile String representation = null;

    protected Generic() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        final Variant variant = Variant.of(genericSuperclass.getActualTypeArguments()[0]);
        rawClass = variant.getRawClass();
        parameters = variant.getParameters();
    }

    @SuppressWarnings("rawtypes")
    public final Class<?> getRawClass() {
        return rawClass;
    }

    @SuppressWarnings("rawtypes")
    public final Map<String, Generic<?>> getParameters() {
        return parameters;
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
            representation = rawClass.getSimpleName() + (
                    parameters.isEmpty() ? "" : parameters.entrySet().stream()
                            .map(Entry::toString)
                            .collect(joining(", ", "<", ">")));
            return representation;
        });
    }
}
