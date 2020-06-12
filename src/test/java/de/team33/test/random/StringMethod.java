package de.team33.test.random;

import de.team33.libs.typing.v4.Type;

import java.util.function.Function;

class StringMethod implements Function<Dispenser, String> {

    private final Function<Dispenser, char[]> backing;

    StringMethod(final Bounds stringBounds) {
        this.backing = new ArrayMethod<>(Type.of(char[].class), stringBounds);
    }

    @Override
    public final String apply(final Dispenser dispenser) {
        return new String(backing.apply(dispenser));
    }
}
