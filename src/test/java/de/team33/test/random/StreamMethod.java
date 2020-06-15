package de.team33.test.random;

import de.team33.libs.typing.v4.Shape;
import de.team33.libs.typing.v4.Type;

import java.util.function.Function;
import java.util.stream.Stream;

class StreamMethod<E> implements Function<Dispenser, Stream<E>> {

    private final Shape elementShape;
    private final Bounds bounds;

    StreamMethod(final Shape shape, final Bounds bounds) {
        this.elementShape = shape.getActualParameters().stream()
                                 .findAny()
                                 .orElseGet(() -> Type.of(Object.class));
        this.bounds = bounds;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Stream<E> apply(final Dispenser dispenser) {
        final int size = bounds.lower + dispenser.basics.anyInt(bounds.distance);
        return newStream(dispenser, size);
    }

    @SuppressWarnings("rawtypes")
    private Stream newStream(final Dispenser dispenser, final int size) {
        return Stream.generate(() -> dispenser.any(elementShape))
                     .limit(size);
    }
}
