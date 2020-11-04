package de.team33.libs.typing.v4.experimental2;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class Cases<I, R> implements Function<I, R> {

    private final Map<Case<I, R>, Function<Cases<I, R>, Function<I, R>>> backing;
    private final Case<I, R> initial;

    private Cases(final Builder<I, R> builder) {
        this.backing = Collections.unmodifiableMap(new HashMap<>(builder.backing));
        this.initial = builder.initial;
    }

    public static <I, R> Initial<I, R> on(final Case<I, R> initial) {
        return new Builder<I, R>(initial).on(initial);
    }

    @Override
    public final R apply(final I input) {
        return apply(initial, input);
    }

    private R apply(final Case<I, R> base, final I input) {
        return Optional.ofNullable(backing.get(base))
                       .orElseThrow(() -> new IllegalStateException("unknown case: " + base))
                       .apply(this)
                       .apply(input);
    }

    public interface Initial<I, R> {

        Condition<I, R> when(Predicate<I> predicate);

        Builder<I, R> apply(Function<I, R> function);
    }

    public interface Condition<I, R> {

        Consequence<I, R> then(Case<I, R> positive);
    }

    public interface Consequence<I, R> {

        Builder<I, R> orElse(Case<I, R> negative);
    }

    public static class Builder<I, R> {

        private final Map<Case<I, R>, Function<Cases<I, R>, Function<I, R>>> backing = new HashMap<>(0);
        private final Set<Object> defined = new HashSet<>(0);
        private final Set<Object> used = new HashSet<>(0);
        private final Case<I, R> initial;

        private Builder(final Case<I, R> initial) {
            this.initial = initial;
            addUsed(initial);
        }

        public final Cases<I, R> build() {
            final Set<Object> undefined = new HashSet<>(used);
            undefined.removeAll(defined);
            if (!undefined.isEmpty()) {
                throw new UndefinedException(undefined);
            }

            final Set<Object> unused = new HashSet<>(defined);
            unused.removeAll(used);
            if (!unused.isEmpty()) {
                throw new UnusedException(unused);
            }

            return new Cases<>(this);
        }

        public final Initial<I, R> on(final Case<I, R> base) {
            return new Stage(base);
        }

        private Builder<I, R> add(final Case<I, R> base,
                                  final Function<I, R> function) {
            backing.put(base, ignored -> function);
            return addDefined(base);
        }

        private Builder<I, R> add(final Case<I, R> base,
                                  final Predicate<? super I> predicate,
                                  final Case<I, R> positive,
                                  final Case<I, R> negative) {
            backing.put(base, cases -> input -> cases.apply(predicate.test(input) ? positive : negative, input));
            return addDefined(base).addUsed(positive, negative);
        }

        private Builder<I, R> addDefined(final Case<I, R> base) {
            if(!defined.add(base)) {
                throw new IllegalStateException("Already defined: " + base);
            }
            return this;
        }

        @SafeVarargs
        private final Builder<I, R> addUsed(final Case<I, R>... cases) {
            used.addAll(Arrays.asList(cases));
            return this;
        }

        private final class Stage implements Initial<I, R> {

            private final Case<I, R> base;

            private Stage(final Case<I, R> base) {
                this.base = base;
            }

            @Override
            public final Condition<I, R> when(final Predicate<I> predicate) {
                return positive -> negative -> add(base, predicate, positive, negative);
            }

            @Override
            public final Builder<I, R> apply(final Function<I, R> function) {
                return add(base, function);
            }
        }
    }
}
