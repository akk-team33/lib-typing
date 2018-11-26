package de.team33.libs.typing.v3;

import java.util.function.Supplier;

class Lazy<T> implements Supplier<T> {

    private volatile Supplier<T> backing;

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
        public final T get() {
            synchronized (this) {
                if (this == backing) {
                    backing = new Final<>(initial.get());
                }
            }
            return backing.get();
        }
    }

    private static final class Final<T> implements Supplier<T> {

        private final T value;

        private Final(final T value) {
            this.value = value;
        }

        @Override
        public final T get() {
            return value;
        }
    }
}
