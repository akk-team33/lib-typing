package de.team33.libs.typing.v4;

import java.lang.reflect.Array;

import static java.util.Collections.singletonList;

class GenericArrayType extends ArrayType {

    GenericArrayType(final java.lang.reflect.GenericArrayType type, final Context context) {
        this(getActualParameter(type, context));
    }

    private GenericArrayType(final RawType actualParameter) {
        super(getPrimeClass(actualParameter), singletonList(actualParameter));
    }

    private static RawType getActualParameter(final java.lang.reflect.GenericArrayType type, final Context context) {
        return TypeMapper.map(type.getGenericComponentType(), context);
    }

    private static Class<?> getPrimeClass(final RawType actualParameter) {
        return Array.newInstance(actualParameter.getPrimeClass(), 0).getClass();
    }
}
