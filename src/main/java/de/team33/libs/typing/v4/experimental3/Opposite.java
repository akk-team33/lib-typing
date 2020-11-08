package de.team33.libs.typing.v4.experimental3;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

final class Opposite<I, R> implements Case<I, R> {

    @SuppressWarnings("rawtypes")
    private static final Map<Case, Opposite> CACHE = new ConcurrentHashMap<>(0);

    private final Case<I, R> original;

    private Opposite(final Case<I, R> original) {
        this.original = original;
    }

    @SuppressWarnings("unchecked")
    static <I, R> Case<I, R> of(final Case<I, R> original) {
        return (original instanceof Opposite)
                ? ((Opposite<I, R>) original).original
                : CACHE.computeIfAbsent(original, Opposite::new);
    }

    @Override
    public final boolean isMatching(final I input) {
        return !original.isMatching(input);
    }

    @Override
    public final Optional<Function<I, R>> getPositive() {
        return original.getNegative();
    }

    @Override
    public final Optional<Function<I, R>> getNegative() {
        return original.getPositive();
    }

    @Override
    public final String toString() {
        return "~" + original;
    }
}
