package de.team33.test.random;

import java.util.Random;

public class DefaultBasics extends Random implements Dispenser.Basics {

    private final char[] defaultCharset;

    public DefaultBasics(final String defaultCharset) {
        this.defaultCharset = defaultCharset.toCharArray();
    }

    @Override
    public final boolean anyBoolean() {
        return nextBoolean();
    }

    @Override
    public final byte anyByte() {
        return (byte) next(Byte.SIZE);
    }

    @Override
    public final short anyShort() {
        return (short) next(Short.SIZE);
    }

    @Override
    public final int anyInt() {
        return nextInt();
    }

    @Override
    public final int anyInt(final int bound) {
        return nextInt(bound);
    }

    @Override
    public final long anyLong() {
        return nextLong();
    }

    @Override
    public final float anyFloat() {
        return nextFloat();
    }

    @Override
    public final double anyDouble() {
        return nextDouble();
    }

    @Override
    public char anyChar() {
        return anyCharOf(defaultCharset);
    }

    @Override
    public char anyCharOf(final char[] charset) {
        return charset[nextInt(charset.length)];
    }
}
