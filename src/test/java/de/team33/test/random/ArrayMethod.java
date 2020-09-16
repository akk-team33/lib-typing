package de.team33.test.random;

import de.team33.libs.typing.v4.RawType;

import java.lang.reflect.Array;
import java.util.function.Function;

class ArrayMethod<A> implements Function<Dispenser, A> {

    private final Class<?> componentType;
    private final Function<Dispenser, Bounds> getBounds;

    ArrayMethod(final RawType setup, final Function<Dispenser, Bounds> getBounds) {
        this.componentType = setup.getPrimeClass().getComponentType();
        this.getBounds = getBounds;
    }

    @Override
    public final A apply(final Dispenser dispenser) {
        final Bounds bounds = getBounds.apply(dispenser);
        final int length = bounds.lower + dispenser.basics.anyInt(bounds.distance);
        final Object result = Array.newInstance(componentType, length);
        for (int index = 0; index < Array.getLength(result); ++index) {
            Array.set(result, index, dispenser.any(componentType));
        }
        //noinspection unchecked
        return (A) result;
    }
}
