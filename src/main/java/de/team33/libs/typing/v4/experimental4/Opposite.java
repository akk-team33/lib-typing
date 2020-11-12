package de.team33.libs.typing.v4.experimental4;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

final class Opposite<I, R> implements Case<I, R> {

    @SuppressWarnings("rawtypes")
    private static final Map<Case, Opposite> CACHE = new ConcurrentHashMap<>(0);
    @SuppressWarnings("rawtypes")
    private static final Predicate FALSE = input -> false;

    private final Case<I, R> original;
    private final Predicate<I> predicate;

    private Opposite(final Case<I, R> original) {
        this.original = original;
        predicate = inverse(original.getCondition().orElse(null));
    }

    @SuppressWarnings("unchecked")
    static <I, R> Case<I, R> of(final Case<I, R> original) {
        return (original instanceof Opposite)
                ? ((Opposite<I, R>) original).original
                : CACHE.computeIfAbsent(original, Opposite::new);
    }

    @SuppressWarnings("unchecked")
    private static <I> Predicate<I> inverse(final Predicate<I> predicate) {
        return (null == predicate) ? FALSE : input -> !predicate.test(input);
    }

    @Override
    public final Case<I, R> getPreCondition() {
        return original.getPreCondition();
    }

    @Override
    public Optional<Predicate<I>> getCondition() {
        return Optional.of(predicate);
    }

    @Override
    public final Optional<Function<I, R>> getMethod() {
        return Optional.empty();
    }

    @Override
    public final String toString() {
        return "not(" + original + ")";
    }
}
