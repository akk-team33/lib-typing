package de.team33.libs.typing.v4.experimental;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableMap;

@SuppressWarnings("InterfaceWithOnlyOneDirectInheritor")
public final class Choices<I, R> implements Function<I, R> {

    private final Map<Id<I, R>, Function<Choices<I, R>, Function<I, R>>> backing;
    private final Supplier<? extends RuntimeException> xSupplier;
    private final Id<I, R> start;

    private Choices(final Builder<I, R> builder, final Supplier<? extends RuntimeException> xSupplier) {
        this.start = builder.start;
        this.backing = unmodifiableMap(new HashMap<>(builder.backing));
        this.xSupplier = xSupplier;
    }

    public static <I, R> Builder<I, R> add(final Choices.Id<I, R> id,
                                           final Predicate<I> condition,
                                           final Choices.Id<I, R> when,
                                           final Choices.Id<I, R> otherwise) {
        return new Builder<>(id).add(id, condition, when, otherwise);
    }

    @Override
    public final R apply(final I input) {
        return apply(start, input);
    }

    private Function<I, R> apply(final Predicate<I> condition, final Id<I, R> when, final Id<I, R> otherwise) {
        return input -> apply(condition.test(input) ? when : otherwise, input);
    }

    private R apply(final Id<I, R> id, final I input) {
        return Optional.ofNullable(backing.get(id))
                       .orElseThrow(xSupplier)
                       .apply(this)
                       .apply(input);
    }

    @SuppressWarnings("MarkerInterface")
    public interface Id<I, R> {
    }

    public static final class Builder<I, R> {

        private final Map<Id<I, R>, Function<Choices<I, R>, Function<I, R>>> backing = new HashMap<>(0);
        private Set<Id<?,?>> defined = new HashSet<>(0);
        private Set<Id<?,?>> used = new HashSet<>(0);
        private final Id<I, R> start;

        private Builder(final Id<I, R> start) {
            this.start = start;
            addUsed(start);
        }

        public final Builder<I, R> add(final Choices.Id<I, R> id,
                                       final Predicate<I> condition,
                                       final Choices.Id<I, R> when,
                                       final Choices.Id<I, R> otherwise) {
            backing.put(id, choices -> choices.apply(condition, when, otherwise));
            return addDefined(id).addUsed(when, otherwise);
        }

        public final Builder<I, R> add(final Choices.Id<I, R> id,
                                       final Function<I, R> function) {
            backing.put(id, choices -> function);
            return addDefined(id);
        }

        private Builder<I, R> addUsed(final Id<?, ?> ... states) {
            Stream.of(states)
                  .filter(Objects::nonNull)
                  .forEach(used::add);
            return this;
        }

        private Builder<I, R> addDefined(final Id<I, R> id) {
            defined.add(id);
            return this;
        }

        public final <X extends RuntimeException> Choices<I, R> orElseThrow(final Supplier<X> supplier) {
            final HashSet<Id<?, ?>> usedButNotDefined = new HashSet<>(used);
            usedButNotDefined.removeAll(defined);
            if (0 < usedButNotDefined.size()) {
                throw new UsedButNotDefinedException(usedButNotDefined);
            }

            final HashSet<Id<?, ?>> definedButNotUsed = new HashSet<>(defined);
            definedButNotUsed.removeAll(used);
            if (0 < definedButNotUsed.size()) {
                throw new DefinedButNotUsedException(definedButNotUsed);
            }

            return new Choices<>(this, supplier);
        }
    }
}
