package de.team33.libs.typing.v4.experimental4;

import de.team33.libs.typing.v4.experimental3.UndefinedException;
import de.team33.libs.typing.v4.experimental3.UnusedException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.team33.libs.typing.v4.experimental4.Case.none;
import static de.team33.libs.typing.v4.experimental4.Case.not;
import static java.util.Collections.unmodifiableMap;

public final class Cases<I, R> implements Function<I, R> {

    private final Map<Case<I, R>, Function<Cases<I, R>, Function<I, R>>> backing;

    private Cases(final Builder<I, R> builder) {
        this.backing = unmodifiableMap(new HashMap<>(builder.backing));
    }

    private static <I, R> Stage<I, R> whenInitial() {
        return new Builder<I, R>().when(none());
    }

    @SafeVarargs
    public static <I, R> Cases<I, R> build(final Case<I, R>... cases) {
        final Builder<I, R> builder = new Builder<>();
        for (final Case<I, R> value : cases) {
            final Case<I, R> preCondition = value.getPreCondition();
            builder.when(preCondition).check(value);
        }
        return builder.build();
    }

    @Override
    public final R apply(final I input) {
        return apply(none(), input);
    }

    private R apply(final Case<I, R> base, final I input) {
        return Optional.ofNullable(backing.get(base))
                       .orElseThrow(() -> new IllegalStateException("unknown case: " + base))
                       .apply(this)
                       .apply(input);
    }

    public static final class Builder<I, R> {

        private final Map<Case<I, R>, Function<Cases<I, R>, Function<I, R>>> backing = new HashMap<>(0);
        private final Set<Object> defined = new HashSet<>(0);
        private final Set<Object> used = new HashSet<>(0);

        private Builder() {
            addUsed(none());
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

        final Stage<I, R> when(final Case<I, R> base) {
            return new Stage<>(this, base);
        }

        private Builder<I, R> addMethod(final Case<I, R> base) {
            return base.getMethod()
                       .map(method -> {
                           backing.put(base, cases -> method);
                           return addDefined(base);
                       })
                       .orElse(this);
        }

        private Builder<I, R> add(final Case<I, R> base,
                                  final Case<I, R> next) {
            final Addition<I, R> addition = new Addition<>(next);
            backing.put(base, addition.method);
            return addDefined(base).addUsed(addition.used);
        }

        private static class Addition<I, R> {

            private final Function<Cases<I, R>, Function<I, R>> method;
            private final Case<I, R>[] used;

            private Addition(final Case<I, R> next) {
                if (next.isDefault()) {
                    method = cases -> input -> cases.apply(next, input);
                    used = new Case[]{next};
                } else {
                    final Case<I, R> notNext = not(next);
                    method = cases -> input -> cases.apply(next.isMatching(input) ? next : notNext, input);
                    used = new Case[]{next, notNext};
                }
            }
        }

        private Builder<I, R> add(final Case<I, R> base,
                                  final Predicate<? super I> predicate,
                                  final Case<I, R> positive,
                                  final Case<I, R> negative) {
            backing.put(base, cases -> input -> cases.apply(predicate.test(input) ? positive : negative, input));
            return addDefined(base).addUsed(positive, negative);
        }

        private Builder<I, R> addDefined(final Case<I, R> base) {
            if (!defined.add(base)) {
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

        final Builder<I, R> check(final Case<I, R> next) {
            return builder.add(base, next)
                          .addMethod(next);
        }
    }
}
