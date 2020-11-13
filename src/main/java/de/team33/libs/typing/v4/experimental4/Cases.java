package de.team33.libs.typing.v4.experimental4;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static de.team33.libs.typing.v4.experimental4.Case.none;
import static de.team33.libs.typing.v4.experimental4.Case.not;
import static java.util.Collections.unmodifiableMap;

public final class Cases<I, R> implements Function<I, R> {

    private final Map<Case<I, R>, Case<I, R>> postConditions;

    private Cases(final Builder<I, R> builder) {
        postConditions = unmodifiableMap(builder.postConditions);
    }

    @SafeVarargs
    public static <I, R> Cases<I, R> build(final Case<I, R>... cases) {
        return Stream.of(cases)
                     .collect(() -> new Builder<I, R>(none()), Builder::add, Builder::addAll)
                     .build();
    }

    @Override
    public final R apply(final I input) {
        return apply(none(), input);
    }

    private R apply(final Case<I, R> base, final I input) {
        return base.getResult().orElseGet(() -> applyPost(base, input));
    }

    private R applyPost(final Case<I, R> base, final I input) {
        return apply(nextCase(postConditions.get(base), input), input);
    }

    private static <I, R> Case<I, R> nextCase(final Case<I, R> subject, final I input) {
        return subject.getCondition()
                      .map(condition -> condition.test(input) ? subject : not(subject))
                      .orElse(subject);
    }

    private static final class Builder<I, R> {

        private final Map<Case<I, R>, Case<I, R>> postConditions = new HashMap<>(0);
        private final Set<Object> defined = new HashSet<>(0);
        private final Set<Object> used = new HashSet<>(0);

        private Builder(final Case<Object, Object> none) {
            used.add(none);
        }

        private void add(final Case<I, R> next) {
            final Case<I, R> pre = next.getPreCondition();
            postConditions.put(pre, next);
            defined.add(pre);
            if (next.getResult().isPresent()) {
                defined.add(next);
            }
            used.add(next);
            if (next.getCondition().isPresent()) {
                used.add(not(next));
            }
        }

        private void addAll(final Builder<I, R> other) {
            throw new UnsupportedOperationException("shouldn't be necessary here");
        }

        public Cases<I, R> build() {
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

            return new Cases<I, R>(this);
        }
    }
}
