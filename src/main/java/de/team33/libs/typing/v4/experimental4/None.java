package de.team33.libs.typing.v4.experimental4;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

final class None<I, R> implements Case<I, R> {

    @SuppressWarnings("rawtypes")
    private static final Case INSTANCE = new None();

    private None() {
    }

    @SuppressWarnings("unchecked")
    static <R, I> Case<I, R> instance() {
        return INSTANCE;
    }

    @Override
    public final Case<I, R> getPreCondition() {
        return this;
    }

    @Override
    public Optional<Predicate<I>> getCondition() {
        return Optional.of(input -> true);
    }

    @Override
    public final Optional<Function<I, R>> getMethod() {
        return Optional.empty();
    }

    @Override
    public final String toString() {
        return "NONE";
    }
}
