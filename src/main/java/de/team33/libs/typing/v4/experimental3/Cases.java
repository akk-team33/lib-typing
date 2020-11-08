package de.team33.libs.typing.v4.experimental3;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.unmodifiableMap;

@SuppressWarnings("ClassWithOnlyPrivateConstructors")
public class Cases<I, R> implements Function<I, R> {

    @SuppressWarnings("rawtypes")
    private static final Case INITIAL = new Case() {

        @Override
        public boolean isMatching(final Object input) {
            return true;
        }

        @Override
        public Optional<Function> getPositive() {
            return Optional.empty();
        }

        @Override
        public Optional<Function> getNegative() {
            return Optional.empty();
        }
    };

    private final Map<Case<I, R>, Function<Cases<I, R>, Function<I, R>>> backing;
    private final Case<I, R> initial;

    private Cases(final Builder<I, R> builder) {
        this.backing = unmodifiableMap(new HashMap<>(builder.backing));
        this.initial = builder.initial;
    }

    @SuppressWarnings("unchecked")
    private static <I, R> Case<I, R> initial() {
        return INITIAL;
    }

    public static <I, R> Builder<I, R> check(final Case<I, R> base) {
        return new Builder<I, R>(initial()).on(initial()).check(base);
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

        public final Stage<I, R> on(final Case<I, R> base) {
            return new Stage<>(this, base);
        }

        private Builder<I, R> addMethod(final Case<I, R> base) {
            return base.getPositive()
                       .map(positive -> {
                           backing.put(base, cases -> positive);
                           return addDefined(base);
                       })
                       .orElse(this);
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
    }

    public static final class Stage<I, R> {

        private final Builder<I, R> builder;
        private final Case<I, R> base;

        private Stage(final Builder<I, R> builder, final Case<I, R> base) {
            this.builder = builder;
            this.base = base;
        }

        public Builder<I, R> check(final Case<I, R> next) {
            final Case<I, R> opposite = next.opposite();
            return builder.add(base, next::isMatching, next, opposite)
                          .addMethod(next)
                          .addMethod(opposite);
        }
    }
}
