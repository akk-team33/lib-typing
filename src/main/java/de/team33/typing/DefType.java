package de.team33.typing;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * <p>
 * Represents a fully defined type, possibly based on a generic class.
 * </p><p>
 * For example, an instance of {@code DefType<Map<String, List<String>>>}
 * represents the type {@code Map<String, List<String>>}.
 * </p><p>
 * To get an instance of (any) DefType, you first need to create a fully defined derivative of DefType.
 * Only this can be instantiated directly.
 * The easiest way to achieve this is to use an anonymous derivation with simultaneous instantiation. Example:
 * </p><pre>
 * final DefType&lt;Map&lt;String, List&lt;String&gt;&gt;&gt; stringToStringListMapType
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
     * @see DefType
     */
    protected DefType() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        final Stage stage = stage(genericSuperclass.getActualTypeArguments()[0], ParameterMap.EMPTY);
        underlyingClass = stage.getUnderlyingClass();
        parameters = stage.getParameters();
    }

    private DefType(final Stage stage) {
        underlyingClass = stage.getUnderlyingClass();
        parameters = stage.getParameters();
    }

    private DefType(final Class<T> simpleClass) {
        underlyingClass = simpleClass;
        parameters = ParameterMap.EMPTY;
    }

    /**
     * @see DefType
     */
    public static <T> DefType<T> of(final Class<T> simpleClass) {
        return new DefType<T>(simpleClass) {
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
        return new DefType(stage(type, parameters)) {
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

    private static Stage stage(final Type type, final ParameterMap parameters) {
        return Stream.of(TypeType.values())
                .filter(typeType -> typeType.matching.test(type)).findAny()
                .map(typeType -> typeType.mapping.apply(type, parameters))
                .orElseThrow(() -> new IllegalArgumentException("Unknown type of Type: " + type.getClass()));
    }

    private enum TypeType {

        CLASS(
                type -> type instanceof Class<?>,
                (type, map) -> new ClassStage((Class<?>) type)),

        PARAMETERIZED_TYPE(
                type -> type instanceof ParameterizedType,
                (type, map) -> new ParameterizedStage((ParameterizedType) type, map)),

        TYPE_VARIABLE(
                type -> type instanceof TypeVariable,
                (type, map) -> new TypeVariableStage((TypeVariable<?>) type, map));

        private final Predicate<Type> matching;
        private final BiFunction<Type, ParameterMap, Stage> mapping;

        TypeType(final Predicate<Type> matching, final BiFunction<Type, ParameterMap, Stage> mapping) {
            this.matching = matching;
            this.mapping = mapping;
        }
    }

    private abstract static class Stage {

        abstract Class<?> getUnderlyingClass();

        abstract ParameterMap getParameters();
    }

    private static final class ClassStage extends Stage {

        private final Class<?> rawClass;

        private ClassStage(final Class<?> rawClass) {
            this.rawClass = rawClass;
        }

        @Override
        Class<?> getUnderlyingClass() {
            return rawClass;
        }

        @Override
        ParameterMap getParameters() {
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return ParameterMap.EMPTY;
        }
    }

    private static final class ParameterizedStage extends Stage {

        private final ParameterizedType type;
        private final ParameterMap parameters;

        private ParameterizedStage(final ParameterizedType type, final ParameterMap parameters) {
            this.type = type;
            this.parameters = parameters;
        }

        @Override
        Class<?> getUnderlyingClass() {
            return (Class<?>) type.getRawType();
        }

        @Override
        ParameterMap getParameters() {
            final List<String> formal = Stream.of(((Class<?>) type.getRawType()).getTypeParameters())
                    .map(TypeVariable::getName)
                    .collect(Collectors.toList());
            final List<DefType<?>> actual = Stream.of(type.getActualTypeArguments())
                    .map(type1 -> stage(type1, parameters))
                    .map(ParameterizedStage::newGeneric)
                    .collect(Collectors.toList());
            return new ParameterMap(formal, actual);
        }

        private static DefType<?> newGeneric(final Stage stage) {
            return new DefType(stage) {
            };
        }
    }

    private static final class TypeVariableStage extends Stage {

        private final DefType<?> definite;

        private TypeVariableStage(final TypeVariable<?> type, final ParameterMap parameters) {
            final String name = type.getName();
            this.definite = Optional.ofNullable(parameters.get(name))
                    .orElseThrow(() -> new IllegalArgumentException(
                            String.format("Variable <%s> not found in parameters %s", name, parameters)));
        }

        @Override
        Class<?> getUnderlyingClass() {
            return definite.getUnderlyingClass();
        }

        @Override
        ParameterMap getParameters() {
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return definite.parameters;
        }
    }
}
