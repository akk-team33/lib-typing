package de.team33.libs.typing.v4.experimental4;

import de.team33.libs.typing.v4.experimental3.UndefinedException;
import de.team33.libs.typing.v4.experimental3.UnusedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    @SafeVarargs
    public static <I, R> Cases<I, R> build(final Case<I, R>... cases) {
        final Builder<I, R> builder = new Builder<>();
        for (final Case<I, R> next : cases) {
            builder.add(next.getPreCondition(), next);
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
            used.add(none());
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

        private void add(final Case<I, R> base,
                         final Case<I, R> next) {
            new Addition(next).put(base);
            next.getMethod()
                .ifPresent(method -> {
                    backing.put(next, cases -> method);
                    defined.add(next);
                });
        }

        private final class Addition {

            private final Function<Cases<I, R>, Function<I, R>> method;
            private final List<Case<I, R>> usedHere;

            private Addition(final Case<I, R> next) {
                final Predicate<I> condition = next.getCondition().orElse(null);
                if (null == condition) {
                    method = cases -> input -> cases.apply(next, input);
                    usedHere = Collections.singletonList(next);
                } else {
                    final Case<I, R> notNext = not(next);
                    method = cases -> input -> cases.apply(condition.test(input) ? next : notNext, input);
                    usedHere = Arrays.asList(next, notNext);
                }
            }

            private void put(final Case<I, R> base) {
                backing.put(base, method);
                defined.add(base);
                used.addAll(usedHere);
            }
        }
    }
}
