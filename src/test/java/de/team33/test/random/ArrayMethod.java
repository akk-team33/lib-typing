package de.team33.test.random;

import de.team33.libs.typing.v4.Model;

import java.lang.reflect.Array;
import java.util.function.Function;

class ArrayMethod<A> implements Function<Dispenser, A> {

    private final Class<?> componentType;
    private final Bounds bounds;

    ArrayMethod(final Model model, final Bounds bounds) {
        this.componentType = model.getRawClass().getComponentType();
        this.bounds = bounds;
    }

    @Override
    public final A apply(final Dispenser dispenser) {
        final int length = bounds.lower + dispenser.basics.anyInt(bounds.distance);
        final Object result = Array.newInstance(componentType, length);
        for (int index = 0; index < Array.getLength(result); ++index) {
            Array.set(result, index, dispenser.any(componentType));
        }
        //noinspection unchecked
        return (A) result;
    }
}
