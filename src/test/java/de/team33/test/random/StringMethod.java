package de.team33.test.random;

import de.team33.libs.typing.v4.Type;

import java.util.function.Function;

class StringMethod implements Function<Dispenser, Object> {

    private final ArrayMethod backing;

    StringMethod(final Bounds stringBounds) {
        this.backing = new ArrayMethod(Type.of(char[].class), stringBounds);
    }

    @Override
    public final Object apply(final Dispenser dispenser) {
        return new String((char[]) backing.apply(dispenser));
    }
}
