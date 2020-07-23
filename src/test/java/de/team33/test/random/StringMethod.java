package de.team33.test.random;

import de.team33.libs.typing.v4.Type;

import java.util.function.Function;

class StringMethod implements Function<Dispenser, String> {

    private final Function<Dispenser, char[]> backing;

    StringMethod(final Function<Dispenser, Bounds> getBounds) {
        this.backing = new ArrayMethod<>(Type.of(char[].class), getBounds);
    }

    @Override
    public final String apply(final Dispenser dispenser) {
        return new String(backing.apply(dispenser));
    }
}
