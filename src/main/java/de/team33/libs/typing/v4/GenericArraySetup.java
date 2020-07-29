package de.team33.libs.typing.v4;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;

import static java.util.Collections.singletonList;

class GenericArraySetup extends ArraySetup {

    GenericArraySetup(final GenericArrayType type, final Setup context) {
        this(getActualParameter(type, context));
    }

    private GenericArraySetup(final Setup actualParameter) {
        super(getPrimeClass(actualParameter), singletonList(actualParameter));
    }

    private static Setup getActualParameter(final GenericArrayType type, final Setup context) {
        return TypeMapper.map(type.getGenericComponentType(), context);
    }

    private static Class<?> getPrimeClass(final Setup actualParameter) {
        return Array.newInstance(actualParameter.getPrimeClass(), 0).getClass();
    }
}
