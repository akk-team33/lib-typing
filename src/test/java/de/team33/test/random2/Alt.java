package de.team33.test.random2;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

class Alt<L, R> {

    private final L left;
    private final R right;

    private Alt(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    static <L, R> Alt<L, R> left(final L left) {
        return new Alt<>(requireNonNull(left), null);
    }

    static <L, R> Alt<L, R> right(final R right) {
        return new Alt<>(null, requireNonNull(right));
    }

    final <X> X apply(final Function<L, X> leftFunction, final Function<R, X> rightFunction) {
        return (null == left) ? rightFunction.apply(right) : leftFunction.apply(left);
    }

    @Override
    public String toString() {
        return apply(Object::toString, Object::toString);
    }
}
