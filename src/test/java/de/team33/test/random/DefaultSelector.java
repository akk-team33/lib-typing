package de.team33.test.random;

import java.lang.reflect.Array;

public class DefaultSelector implements Dispenser.Selector {

    private final Dispenser.Basics basics;

    public DefaultSelector(final Dispenser.Basics basics) {
        this.basics = basics;
    }

    @Override
    public boolean anyOf(final boolean[] values) {
        return (boolean) anyRawOf(values);
    }

    @Override
    public byte anyOf(final byte[] values) {
        return (byte) anyRawOf(values);
    }

    @Override
    public short anyOf(final short[] values) {
        return (short) anyRawOf(values);
    }

    @Override
    public int anyOf(final int[] values) {
        return (int) anyRawOf(values);
    }

    @Override
    public long anyOf(final long[] values) {
        return (long) anyRawOf(values);
    }

    @Override
    public float anyOf(final float[] values) {
        return (float) anyRawOf(values);
    }

    @Override
    public double anyOf(final double[] values) {
        return (double) anyRawOf(values);
    }

    @Override
    public char anyOf(final char[] values) {
        return (char) anyRawOf(values);
    }

    @Override
    public <T> T anyOf(final T[] values) {
        //noinspection unchecked
        return (T) anyRawOf(values);
    }

    private Object anyRawOf(final Object array) {
        final int index = basics.anyInt(Array.getLength(array));
        return Array.get(array, index);
    }
}
