package de.team33.test.typing.shared;

import java.util.List;
import java.util.Map;

@SuppressWarnings("ClassWithTooManyFields")
public class Generic<T, U, V> {

    public int intField;
    public String stringField;
    public T tField;
    public U uField;
    public V vField;

    public int[] intArray;
    public String[] stringArray;
    public T[] tArray;
    public U[] uArray;
    public V[] vArray;

    public List<T> tList;
    public Map<T, U> t2uMap;
}
