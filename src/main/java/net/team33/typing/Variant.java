package net.team33.typing;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("rawtypes")
abstract class Variant {

    static Variant of(final Type type, final Map<String, Generic<?>> parameters) {
        return Stream.of(Selection.values())
                .filter(selection -> selection.matching.test(type))
                .findAny()
                .map(selection -> selection.mapping.apply(type, parameters))
                .orElseThrow(() -> new IllegalArgumentException("Unspecified Type: " + type.getClass()));
    }

    abstract Class<?> getRawClass();

    abstract Map<String, Generic<?>> getParameters();

    private enum Selection {

        SIMPLE_CLASS(
                type -> type instanceof Class<?>,
                (type, map) -> new Simple((Class<?>) type)),

        PARAMETERIZED_TYPE(
                type -> type instanceof ParameterizedType,
                (type, map) -> new Parameterized((ParameterizedType) type, map)),

        TYPE_VARIABLE(
                type -> type instanceof TypeVariable,
                (type, map) -> new Variable((TypeVariable<?>) type, map));

        private final Predicate<Type> matching;
        private final BiFunction<Type, Map<String, Generic<?>>, Variant> mapping;

        Selection(final Predicate<Type> matching, final BiFunction<Type, Map<String, Generic<?>>, Variant> mapping) {
            this.matching = matching;
            this.mapping = mapping;
        }
    }

    private static final class Simple extends Variant {

        private final Class<?> rawClass;

        private Simple(final Class<?> rawClass) {
            this.rawClass = rawClass;
        }

        @Override
        Class<?> getRawClass() {
            return rawClass;
        }

        @Override
        Map<String, Generic<?>> getParameters() {
            return Collections.emptyMap();
        }
    }

    private static final class Parameterized extends Variant {

        private final ParameterizedType type;
        private final Map<String, Generic<?>> parameters;

        private Parameterized(final ParameterizedType type, final Map<String, Generic<?>> parameters) {
            this.type = type;
            this.parameters = parameters;
        }

        @Override
        Class<?> getRawClass() {
            return (Class<?>) type.getRawType();
        }

        @Override
        Map<String, Generic<?>> getParameters() {
            final List<String> formal = Stream.of(((Class<?>) type.getRawType()).getTypeParameters())
                    .map(TypeVariable::getName)
                    .collect(Collectors.toList());
            final List<Generic<?>> actual = Stream.of(type.getActualTypeArguments())
                    .map(type1 -> of(type1, parameters))
                    .map(Parameterized::newGeneric)
                    .collect(Collectors.toList());
            return new ParameterMap(formal, actual);
        }

        private static Generic<?> newGeneric(final Variant variant) {
            return new Generic(variant) {
            };
        }
    }

    private static final class Variable extends Variant {

        private final Generic<?> generic;

        private Variable(final TypeVariable<?> type, final Map<String, Generic<?>> parameters) {
            final String name = type.getName();
            this.generic = Optional.ofNullable(parameters.get(name))
                    .orElseThrow(() -> new IllegalArgumentException(
                            String.format("Variable <%s> not found in parameters %s", name, parameters)));
        }

        @Override
        Class<?> getRawClass() {
            return generic.getRawClass();
        }

        @Override
        Map<String, Generic<?>> getParameters() {
            return generic.getParameters();
        }
    }
}
