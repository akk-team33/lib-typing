package de.team33.test.typing.shared;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "InterfaceNeverImplemented"})
public interface Interface<T, U, V, R extends Interface<T, U, V, R>> {

    int getIntField();

    R setIntField(final int intField);

    String getStringField();

    R setStringField(final String stringField);

    T getTField();

    R setTField(final T tField);

    U getUField();

    R setUField(final U uField);

    V getVField();

    R setVField(final V vField);

    int[] getIntArray();

    R setIntArray(final int[] intArray);

    String[] getStringArray();

    R setStringArray(final String[] stringArray);

    T[] getTArray();

    R setTArray(final T[] tArray);

    U[] getUArray();

    R setUArray(final U[] uArray);

    V[] getVArray();

    R setVArray(final V[] vArray);

    List<T> getTList();

    R setTList(final List<T> tList);

    Map<T, U> getT2UMap();

    R setT2UMap(final Map<T, U> t2uMap);
}
