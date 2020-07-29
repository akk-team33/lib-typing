package de.team33.libs.typing.v4;

import java.util.function.Supplier;

class Lazy<T> implements Supplier<T> {

    private Supplier<T> backing;

    Lazy(final Supplier<T> initial) {
        this.backing = new Initial(initial);
    }

    @Override
    public final T get() {
        return backing.get();
    }

    private final class Initial implements Supplier<T> {

        private final Supplier<T> initial;

        private Initial(final Supplier<T> initial) {
            this.initial = initial;
        }

        @Override
        public final synchronized T get() {
            if (backing == this) {
                final T result = initial.get();
                backing = () -> result;
            }
            return backing.get();
        }
    }
}
