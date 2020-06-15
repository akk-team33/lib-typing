package de.team33.test.random;

import de.team33.libs.typing.v4.Model;
import de.team33.libs.typing.v4.Type;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ListMethod<E> implements Function<Dispenser, ArrayList<E>> {

    private final Model elementModel;
    private final Bounds bounds;

    ListMethod(final Model model, final Bounds bounds) {
        this.elementModel = model.getActualParameters().stream()
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
        return Stream.generate(() -> dispenser.any(elementModel))
                     .limit(size)
                     .collect(Collectors.toCollection(ArrayList::new));
    }
}
