package net.team33.typing;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("rawtypes")
abstract class Variant {

    static Variant of(final Type type) {
        return Stream.of(Selection.values())
                .filter(selection -> selection.matching.test(type))
                .findAny()
                .map(selection -> selection.mapping.apply(type))
                .orElseThrow(() -> new IllegalArgumentException("Unspecified Type: " + type.getClass()));
    }

    abstract Class<?> getRawClass();

    abstract Map<String, Generic<?>> getParameters();

    private enum Selection {

        SIMPLE_CLASS(
                type -> type instanceof Class<?>,
                type -> new Simple((Class<?>) type)),

        PARAMETERIZED_TYPE(
                type -> type instanceof ParameterizedType,
                type -> new Parameterized((ParameterizedType) type)),

        TYPE_VARIABLE(
                type -> type instanceof TypeVariable,
                type -> new Variable((TypeVariable<?>) type));

        private final Predicate<Type> matching;
        private final Function<Type, Variant> mapping;

        Selection(final Predicate<Type> matching, final Function<Type, Variant> mapping) {
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

        private Parameterized(final ParameterizedType type) {
            this.type = type;
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
                    .map(Variant::of)
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

        private final TypeVariable<?> type;

        private Variable(final TypeVariable<?> type) {
            this.type = type;
        }

        @Override
        Class<?> getRawClass() {
            throw new UnsupportedOperationException("not yet implemented");
        }

        @Override
        Map<String, Generic<?>> getParameters() {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }
}
