package de.team33.libs.typing.v4;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

final class RawTypes_ {

    private RawTypes_() {
    }

    static RawType map(final Type type) {
        return map(type, Context.NULL);
    }

    static RawType map(final Type type, final Context context) {
        return map(null, new Input(type, context));
    }

    private static RawType map(final Id pred, final Input input) {
        return Stream.of(Mapping.values())
                     .filter(mapping -> mapping.isSuccessor(pred))
                     .filter(mapping -> mapping.isMatching(input.type))
                     .findFirst()
                     .map(mapping -> mapping.mapper.apply(input))
                     .orElseThrow(() -> new IllegalArgumentException("Unknown: " + input.type));
    }

    private enum Id {

        CLASS,
        PLAIN_CLASS,
        ARRAY_CLASS,
        PARAMETERIZED,
        TYPE_VARIABLE,
        GENERIC_ARRAY;
    }

    private enum Mapping {

        CLASS(null, Class.class::isInstance, input -> map(Id.CLASS, input)),

        PLAIN_CLASS(Id.CLASS,
                    type -> false == ((Class<?>) type).isArray(),
                    input -> new PlainClassType((Class<?>) input.type)),

        ARRAY_CLASS(Id.CLASS,
                    type -> ((Class<?>) type).isArray(),
                    input -> new PlainArrayType((Class<?>) input.type)),

        PARAMETERIZED(null,
                      java.lang.reflect.ParameterizedType.class::isInstance,
                      input -> new ParameterizedType((java.lang.reflect.ParameterizedType) input.type, input.context)),

        TYPE_VARIABLE(null,
                      TypeVariable.class::isInstance,
                      input -> typeVariableType((TypeVariable<?>) input.type, input.context)),

        GENERIC_ARRAY(null,
                      java.lang.reflect.GenericArrayType.class::isInstance,
                      input -> new GenericArrayType((java.lang.reflect.GenericArrayType) input.type, input.context));

        private final Id id;
        private final Id pred;
        private final Predicate<Type> match;
        private final Function<Input, RawType> mapper;
        Mapping(final Id pred, final Predicate<Type> match, final Function<Input, RawType> mapper) {
            this.id = Id.valueOf(name());
            this.pred = pred;
            this.match = match;
            this.mapper = mapper;
        }

        private static RawType typeVariableType(final TypeVariable<?> type, final Context context) {
            return context.getActual(type.getName());
        }

        final boolean isSuccessor(final Id pred) {
            return (pred == null) ? (this.pred == null) : (this.pred == pred);
        }

        final boolean isMatching(final Type type) {
            return match.test(type);
        }
    }

    private static final class Input {

        private final Type type;
        private final Context context;

        private Input(final Type type, final Context context) {
            this.type = type;
            this.context = context;
        }
    }
}
