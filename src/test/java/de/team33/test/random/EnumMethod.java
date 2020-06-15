package de.team33.test.random;

import de.team33.libs.typing.v4.Model;

import java.util.function.Function;

class EnumMethod<E extends Enum<E>> implements Function<Dispenser, E> {

    private final E[] values;

    EnumMethod(final Model model) {
        //noinspection unchecked
        values = ((Class<E>) model.getRawClass()).getEnumConstants();
    }

    @Override
    public E apply(final Dispenser dispenser) {
        return dispenser.selector.anyOf(values);
    }
}
