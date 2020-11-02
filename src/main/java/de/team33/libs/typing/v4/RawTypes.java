package de.team33.libs.typing.v4;

import de.team33.libs.typing.v4.experimental.Choices;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

enum RawTypes implements Choices.Id<TypeContext, RawType> {

    UNKNOWN,
    CLASS,
    PLAIN_CLASS,
    ARRAY_CLASS,
    GENERIC,
    UNUSED,
    PARAMETERIZED,
    OTHER_GENERIC,
    GENERIC_ARRAY,
    VARIABLE_TYPE,
    TYPE_VARIABLE;

    static RawType map(final Type type) {
        return map(type, Context.NULL);
    }

    static RawType map(final Type type, final Context context) {
        return choices.apply(new TypeContext(type, context));
    }

    private static RawType typeVariableType(final TypeVariable<?> type, final Context context) {
        return context.getActual(type.getName());
    }

    private static final Choices<TypeContext, RawType> choices = Choices
            .add(UNKNOWN, input -> input.type instanceof Class<?>, CLASS, GENERIC)
            .add(CLASS, input -> ((Class<?>)input.type).isArray(), ARRAY_CLASS, PLAIN_CLASS)
            .add(PLAIN_CLASS, input -> new PlainClassType((Class<?>) input.type))
            .add(ARRAY_CLASS, input -> new PlainArrayType((Class<?>) input.type))
            .add(GENERIC, input -> input.type instanceof java.lang.reflect.ParameterizedType,
                 PARAMETERIZED, OTHER_GENERIC)
            .add(PARAMETERIZED, input -> new ParameterizedType((java.lang.reflect.ParameterizedType) input.type,
                                                               input.context))
            .add(OTHER_GENERIC, input -> input.type instanceof GenericArrayType, GENERIC_ARRAY, VARIABLE_TYPE)
            .add(GENERIC_ARRAY, input -> new de.team33.libs.typing.v4.GenericArrayType((GenericArrayType) input.type,
                                                                                       input.context))
            .add(VARIABLE_TYPE, input -> input.type instanceof TypeVariable<?>, TYPE_VARIABLE, null)
            .add(TYPE_VARIABLE, input -> typeVariableType((TypeVariable<?>) input.type, input.context))
            .orElseThrow(IllegalArgumentException::new);

}
