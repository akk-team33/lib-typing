package de.team33.test.random;

import de.team33.libs.typing.v4.Setup;
import de.team33.libs.typing.v4.Type;

import java.util.function.Function;
import java.util.stream.Stream;

class StreamMethod implements Function<Dispenser, Stream> {

    private final Setup elementSetup;
    private final Function<Dispenser, Bounds> getBounds;

    StreamMethod(final Setup setup, final Function<Dispenser, Bounds> getBounds) {
        this.elementSetup = setup.getActualParameters().stream()
                                 .findAny()
                                 .orElseGet(() -> Type.of(Object.class));
        this.getBounds = getBounds;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public final Stream apply(final Dispenser dispenser) {
        final Bounds bounds = getBounds.apply(dispenser);
        final int size = bounds.lower + dispenser.basics.anyInt(bounds.distance);
        return newStream(dispenser, size);
    }

    @SuppressWarnings("rawtypes")
    private Stream newStream(final Dispenser dispenser, final int size) {
        return Stream.generate(() -> dispenser.any(elementSetup))
                     .limit(size);
    }
}
