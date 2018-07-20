package net.team33.typing;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("rawtypes")
abstract class Variant {

    static Variant of(final Type type, final Parameters parameters) {
        return Stream.of(Selection.values())
                .filter(selection -> selection.matching.test(type))
                .findAny()
                .map(selection -> selection.mapping.apply(type, parameters))
                .orElseThrow(() -> new IllegalArgumentException("Unspecified Type: " + type.getClass()));
    }

    abstract Class<?> getRawClass();

    abstract Parameters getParameters();

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
        private final BiFunction<Type, Parameters, Variant> mapping;

        Selection(final Predicate<Type> matching, final BiFunction<Type, Parameters, Variant> mapping) {
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
        Parameters getParameters() {
            return Parameters.EMPTY;
        }
    }

    private static final class Parameterized extends Variant {

        private final ParameterizedType type;
        private final Parameters parameters;

        private Parameterized(final ParameterizedType type, final Parameters parameters) {
            this.type = type;
            this.parameters = parameters;
        }

        @Override
        Class<?> getRawClass() {
            return (Class<?>) type.getRawType();
        }

        @Override
        Parameters getParameters() {
            final List<String> formal = Stream.of(((Class<?>) type.getRawType()).getTypeParameters())
                    .map(TypeVariable::getName)
                    .collect(Collectors.toList());
            final List<DefiniteType<?>> actual = Stream.of(type.getActualTypeArguments())
                    .map(type1 -> of(type1, parameters))
                    .map(Parameterized::newGeneric)
                    .collect(Collectors.toList());
            return new Parameters(formal, actual);
        }

        private static DefiniteType<?> newGeneric(final Variant variant) {
            return new DefiniteType(variant) {
            };
        }
    }

    private static final class Variable extends Variant {

        private final DefiniteType<?> definite;

        private Variable(final TypeVariable<?> type, final Parameters parameters) {
            final String name = type.getName();
            this.definite = Optional.ofNullable(parameters.get(name))
                    .orElseThrow(() -> new IllegalArgumentException(
                            String.format("Variable <%s> not found in parameters %s", name, parameters)));
        }

        @Override
        Class<?> getRawClass() {
            return definite.getRawClass();
        }

        @Override
        Parameters getParameters() {
            return definite.getParameters();
        }
    }
}
