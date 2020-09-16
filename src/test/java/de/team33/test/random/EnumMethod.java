package de.team33.test.random;

import de.team33.libs.typing.v4.RawType;

import java.util.function.Function;

class EnumMethod<E extends Enum<E>> implements Function<Dispenser, E> {

    private final E[] values;

    EnumMethod(final RawType setup) {
        //noinspection unchecked
        values = ((Class<E>) setup.getPrimeClass()).getEnumConstants();
    }

    @Override
    public E apply(final Dispenser dispenser) {
        return dispenser.selector.anyOf(values);
    }
}
