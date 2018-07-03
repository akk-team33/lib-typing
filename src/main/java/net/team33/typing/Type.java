package net.team33.typing;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@SuppressWarnings({
        "AbstractClassWithOnlyOneDirectInheritor",
        "AbstractClassWithoutAbstractMethods",
        "AnonymousInnerClass",
        "unused"})
public abstract class Type<T> {

    @SuppressWarnings("rawtypes")
    private final Class rawClass;
    @SuppressWarnings("rawtypes")
    private final List<Type> parameters;

    private transient volatile String representation = null;

    @SuppressWarnings("WeakerAccess")
    protected Type() {
        try {
            final Compound compound = new Compound(typeArgument(getClass()), Collections.emptyMap());
            this.rawClass = compound.rawClass;
            this.parameters = toTypes(compound.parameters);
        } catch (final RuntimeException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private Type(final Class<T> aClass) {
        this.rawClass = aClass;
        this.parameters = emptyList();
    }

    private Type(final Compound compound) {
        this.rawClass = compound.rawClass;
        this.parameters = toTypes(compound.parameters);
    }

    @SuppressWarnings("rawtypes")
    private static List<Type> toTypes(final Collection<Compound> parameters) {
        return unmodifiableList(parameters.stream()
                .map(cmp -> new Type(cmp) {
                })
                .collect(toList()));
    }

    private static java.lang.reflect.Type typeArgument(final Class<?> thisClass) {
        final java.lang.reflect.Type type = thisClass.getGenericSuperclass();
        return direct(parameterized(type)).getActualTypeArguments()[0];
    }

    private static ParameterizedType direct(final ParameterizedType parameterized) {
        if (Type.class.equals(parameterized.getRawType())) {
            return parameterized;
        } else {
            throw new IllegalArgumentException("The type of an instance must be derived directly from <Type>");
        }
    }

    private static ParameterizedType parameterized(final java.lang.reflect.Type type) {
        if (type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        } else {
            throw new IllegalArgumentException(
                    "The type of an instance must be a concretely parameterized derivation of <Type>");
        }
    }

    public static <T> Type<T> of(final Class<T> aClass) {
        return new Type<T>(aClass) {
        };
    }

    @SuppressWarnings("rawtypes")
    public final Class getRawClass() {
        return rawClass;
    }

    @SuppressWarnings("rawtypes")
    public final List<Type> getParameters() {
        // is already unmodifiable ...
        // noinspection AssignmentOrReturnOfFieldWithMutableType
        return parameters;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(rawClass, parameters);
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof Type) && isEqual((Type<?>) obj));
    }

    private boolean isEqual(final Type<?> other) {
        return rawClass.equals(other.rawClass) && parameters.equals(other.parameters);
    }

    @Override
    public final String toString() {
        return Optional.ofNullable(representation).orElseGet(() -> {
            representation = rawClass.getSimpleName() + (
                    parameters.isEmpty() ? "" : parameters.stream()
                            .map(Type::toString)
                            .collect(joining(", ", "<", ">")));
            return representation;
        });
    }

    private enum Spec {

        CLASS {
            @Override
            boolean matches(final java.lang.reflect.Type type) {
                return type instanceof Class;
            }

            @Override
            Class<?> rawClass(final java.lang.reflect.Type type, final Map<String, Compound> map) {
                return (Class<?>) type;
            }

            @Override
            List<Compound> parameters(final java.lang.reflect.Type type, final Map<String, Compound> map) {
                return emptyList();
            }
        },

        PARAMETERIZED_TYPE {
            @Override
            boolean matches(final java.lang.reflect.Type type) {
                return type instanceof ParameterizedType;
            }

            @Override
            Class<?> rawClass(final java.lang.reflect.Type type, final Map<String, Compound> map) {
                return (Class<?>) ((ParameterizedType) type).getRawType();
            }

            @Override
            List<Compound> parameters(final java.lang.reflect.Type type, final Map<String, Compound> map) {
                return Stream.of(((ParameterizedType) type).getActualTypeArguments())
                        .map(arg -> new Compound(arg, map))
                        .collect(toList());
            }
        },

        TYPE_VARIABLE {
            @Override
            boolean matches(final java.lang.reflect.Type type) {
                return type instanceof TypeVariable;
            }

            @Override
            Class<?> rawClass(final java.lang.reflect.Type type, final Map<String, Compound> map) {
                return Optional.ofNullable(map.get(((TypeVariable<?>) type).getName()))
                        .map(Compound::getRawClass)
                        .orElseThrow(() -> new IllegalStateException(
                                String.format("<%s> is not in %s", type, map)));
            }

            @Override
            List<Compound> parameters(final java.lang.reflect.Type type, final Map<String, Compound> map) {
                return map.get(type.getTypeName()).getParameters();
            }
        };

        @SuppressWarnings({"StaticMethodOnlyUsedInOneClass", "ChainOfInstanceofChecks"})
        public static Spec valueOf(final java.lang.reflect.Type type) {
            return Stream.of(values())
                    .filter(value -> value.matches(type))
                    .findAny()
                    .orElseThrow(() ->
                            new IllegalArgumentException("Unsupported type: " + type.getClass().getCanonicalName()));
        }

        abstract boolean matches(final java.lang.reflect.Type type);

        abstract Class<?> rawClass(final java.lang.reflect.Type type, final Map<String, Compound> map);

        abstract List<Compound> parameters(final java.lang.reflect.Type type, final Map<String, Compound> map);
    }

    private static class Compound {

        @SuppressWarnings("rawtypes")
        private final Class rawClass;
        private final List<Compound> parameters;
        private transient volatile String presentation;

        private Compound(final Class<?> rawClass, final List<Compound> parameters) {
            this.rawClass = rawClass;
            final int expectedLength = rawClass.getTypeParameters().length;
            final int actualLength = parameters.size();
            if (0 == actualLength) {
                this.parameters = Collections.emptyList();
            } else if (expectedLength == actualLength) {
                this.parameters = unmodifiableList(new ArrayList<>(parameters));
            } else {
                throw new IllegalArgumentException(
                        String.format(
                                "class %s needs %d type parameter(s) but was %d",
                                rawClass.getCanonicalName(), expectedLength, actualLength));
            }
        }

        private Compound(final java.lang.reflect.Type type, final Spec spec, final Map<String, Compound> map) {
            this(spec.rawClass(type, map), spec.parameters(type, map));
        }

        @SuppressWarnings("OverloadedVarargsMethod")
        public Compound(final Class<?> rawClass, final Compound... parameters) {
            this(rawClass, asList(parameters));
        }

        public Compound(final java.lang.reflect.Type type, final Map<String, Compound> map) {
            this(type, Spec.valueOf(type), map);
        }

        @SuppressWarnings("rawtypes")
        public final Class getRawClass() {
            return rawClass;
        }

        public final List<Compound> getParameters() {
            // is already unmodifiable ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return parameters;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(rawClass, parameters);
        }

        @Override
        public final boolean equals(final Object obj) {
            return (this == obj) || ((obj instanceof Type.Compound) && isEqual((Compound) obj));
        }

        private boolean isEqual(final Compound other) {
            return rawClass.equals(other.rawClass) && parameters.equals(other.parameters);
        }

        @Override
        public final String toString() {
            return Optional.ofNullable(presentation).orElseGet(() -> {
                presentation = rawClass.getSimpleName() + (
                        parameters.isEmpty() ? "" : parameters.stream()
                                .map(Compound::toString)
                                .collect(joining(", ", "<", ">")));
                return presentation;
            });
        }
    }
}
