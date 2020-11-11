package de.team33.libs.typing.v4.experimental4;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class Opposite<I, R> implements Case<I, R> {

    @SuppressWarnings("rawtypes")
    private static final Map<Case, Opposite> CACHE = new ConcurrentHashMap<>(0);
    private static final String INDEFINITE =
            "this case (%s) is not definite, so this call cannot lead to a regular result";

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
    public final Case<I, R> getPreCondition() {
        return original.getPreCondition();
    }

    @Override
    public final boolean isDefault() {
        return false;
    }

    @Override
    public final boolean isMatching(final I input) {
        return !original.isMatching(input);
    }

    @Override
    public final boolean isDefinite() {
        return false;
    }

    @Override
    public final R apply(final I input) {
        throw new UnsupportedOperationException(String.format(INDEFINITE, this));
    }

    @Override
    public final String toString() {
        return "~" + original;
    }
}
