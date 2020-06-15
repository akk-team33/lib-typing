package de.team33.test.random;

import de.team33.libs.typing.v4.Shape;
import de.team33.libs.typing.v4.Type;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ListMethod<E> implements Function<Dispenser, ArrayList<E>> {

    private final Shape elementShape;
    private final Bounds bounds;

    ListMethod(final Shape shape, final Bounds bounds) {
        this.elementShape = shape.getActualParameters().stream()
                                 .findAny()
                                 .orElseGet(() -> Type.of(Object.class));
        this.bounds = bounds;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final ArrayList<E> apply(final Dispenser dispenser) {
        final int size = bounds.lower + dispenser.basics.anyInt(bounds.distance);
        return newList(dispenser, size);
    }

    @SuppressWarnings("rawtypes")
    private ArrayList newList(final Dispenser dispenser, final int size) {
        return Stream.generate(() -> dispenser.any(elementShape))
                     .limit(size)
                     .collect(Collectors.toCollection(ArrayList::new));
    }
}
