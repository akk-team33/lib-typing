package de.team33.libs.typing.v2;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

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
public abstract class DefType<T> extends TypeDesc {

    @SuppressWarnings("rawtypes")
    private final Class<?> underlyingClass;
    @SuppressWarnings("rawtypes")
    private final ParameterMap parameters;

    /**
     * Initializes a {@link DefType} based on its own full definition
     */
    protected DefType() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        final Stage stage = stage(genericSuperclass.getActualTypeArguments()[0], ParameterMap.EMPTY);
        underlyingClass = stage.getUnderlyingClass();
        parameters = newParameters(stage);
    }

    private DefType(final Stage stage) {
        underlyingClass = stage.getUnderlyingClass();
        parameters = newParameters(stage);
    }

    private static ParameterMap newParameters(final Stage stage) {
        return new ParameterMap(
                stage.getFormalParameters(),
                stage.getActualParameters()
        );
    }

    /**
     * Returns a {@link DefType} based on a simple, fully defined {@link Class}.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static <T> DefType<T> of(final Class<T> simpleClass) {
        return new DefType<T>(new ClassStage(simpleClass)) {
        };
    }

    private static Stage stage(final Type type, final ParameterMap parameters) {
        return Stream.of(TypeType.values())
                .filter(typeType -> typeType.matching.test(type)).findAny()
                .map(typeType -> typeType.mapping.apply(type, parameters))
                .orElseThrow(() -> new IllegalArgumentException("Unknown type of Type: " + type.getClass()));
    }

    private static ParameterMap newArrayParameterMap(final TypeDesc componentType) {
        return new ParameterMap(
                singletonList("E"),
                singletonList(componentType)
        );
    }

    @Override
    public final Class<?> getUnderlyingClass() {
        return underlyingClass;
    }

    @Override
    public final Map<String, TypeDesc> getParameters() {
        // noinspection AssignmentOrReturnOfFieldWithMutableType
        return parameters;
    }

    @Override
    public final List<String> getFormalParameters() {
        return parameters.getFormal();
    }

    @Override
    public final List<TypeDesc> getActualParameters() {
        return parameters.getActual();
    }

    @Override
    public final TypeDesc getMemberType(final Type type) {
        return new DefType(stage(type, parameters)) {
        };
    }

    private enum TypeType {

        CLASS(
                type -> type instanceof Class<?>,
                (type, map) -> new ClassStage((Class<?>) type)),

        GENERIC_ARRAY(
                type -> type instanceof GenericArrayType,
                ((type, map) -> new ArrayStage((GenericArrayType) type, map))
        ),

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

    private static final class ClassStage extends Stage {

        private final Class<?> underlyingClass;
        private final ParameterMap parameters;

        private ClassStage(final Class<?> underlyingClass) {
            this.underlyingClass = underlyingClass;
            this.parameters = underlyingClass.isArray()
                    ? newArrayParameterMap(of(underlyingClass.getComponentType()))
                    : ParameterMap.EMPTY;
        }

        @Override
        final Class<?> getUnderlyingClass() {
            return underlyingClass;
        }

        @Override
        final List<String> getFormalParameters() {
            return parameters.getFormal();
        }

        @Override
        final List<TypeDesc> getActualParameters() {
            return parameters.getActual();
        }
    }

    private static final class ArrayStage extends Stage {

        private final TypeDesc componentType;
        private final ParameterMap parameterMap;

        @SuppressWarnings("AnonymousInnerClassMayBeStatic")
        private ArrayStage(final GenericArrayType type, final ParameterMap context) {
            this.componentType = new DefType(stage(type.getGenericComponentType(), context)) {
            };
            this.parameterMap = newArrayParameterMap(componentType);
        }

        private static Class<?> arrayClass(final Class<?> componentClass) {
            return Array.newInstance(componentClass, 0).getClass();
        }

        @Override
        final Class<?> getUnderlyingClass() {
            return arrayClass(componentType.getUnderlyingClass());
        }

        @Override
        final List<String> getFormalParameters() {
            return parameterMap.getFormal();
        }

        @Override
        final List<TypeDesc> getActualParameters() {
            return parameterMap.getActual();
        }
    }

    private static final class ParameterizedStage extends Stage {

        private final ParameterizedType type;
        private final ParameterMap parameters;

        private ParameterizedStage(final ParameterizedType type, final ParameterMap context) {
            this.type = type;
            final List<String> formal = Stream.of(((Class<?>) type.getRawType()).getTypeParameters())
                    .map(TypeVariable::getName)
                    .collect(Collectors.toList());
            final List<TypeDesc> actual = Stream.of(type.getActualTypeArguments())
                    .map(type1 -> stage(type1, context))
                    .map(ParameterizedStage::newGeneric)
                    .collect(Collectors.toList());
            this.parameters = new ParameterMap(formal, actual);
        }

        private static TypeDesc newGeneric(final Stage stage) {
            return new DefType(stage) {
            };
        }

        @Override
        final Class<?> getUnderlyingClass() {
            return (Class<?>) type.getRawType();
        }

        @Override
        final List<String> getFormalParameters() {
            return parameters.getFormal();
        }

        @Override
        final List<TypeDesc> getActualParameters() {
            return parameters.getActual();
        }
    }

    private static final class TypeVariableStage extends Stage {

        private final TypeDesc definite;

        private TypeVariableStage(final TypeVariable<?> type, final ParameterMap context) {
            final String name = type.getName();
            this.definite = Optional.ofNullable(context.get(name))
                    .orElseThrow(() -> new IllegalArgumentException(
                            String.format("Variable <%s> not found in parameters %s", name, context)));
        }

        @Override
        final Class<?> getUnderlyingClass() {
            return definite.getUnderlyingClass();
        }

        @Override
        final List<String> getFormalParameters() {
            return definite.getFormalParameters();
        }

        @Override
        final List<TypeDesc> getActualParameters() {
            return definite.getActualParameters();
        }
    }
}
