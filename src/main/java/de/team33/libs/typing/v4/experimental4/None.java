package de.team33.libs.typing.v4.experimental4;

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
    public final boolean isDefault() {
        return false;
    }

    @Override
    public final boolean isMatching(final I input) {
        return true;
    }

    @Override
    public final boolean isDefinite() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public final R apply(final I input) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public final String toString() {
        return "None";
    }
}
