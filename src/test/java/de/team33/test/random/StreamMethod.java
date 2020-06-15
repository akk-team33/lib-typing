package de.team33.test.random;

import de.team33.libs.typing.v4.Model;
import de.team33.libs.typing.v4.Type;

import java.util.function.Function;
import java.util.stream.Stream;

class StreamMethod<E> implements Function<Dispenser, Stream<E>> {

    private final Model elementModel;
    private final Bounds bounds;

    StreamMethod(final Model model, final Bounds bounds) {
        this.elementModel = model.getActualParameters().stream()
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
        return Stream.generate(() -> dispenser.any(elementModel))
                     .limit(size);
    }
}
