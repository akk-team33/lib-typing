package de.team33.libs.typing.v4.experimental;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class XChoices<I, R, X extends Exception> implements XFunction<I, R, X> {

    private final Id<I, R, X> start;
    private final Map<Id<?, ?, ?>, Function<XChoices<I, R, X>, XFunction<I, R, X>>> backing;

    private XChoices(final Id<I, R, X> start) {
        this.start = start;
        throw new UnsupportedOperationException("not yet implemented");
    }

    private XChoices(final Stage<I, R, X> stage) {
        start = stage.start;
        backing = Collections.unmodifiableMap(new HashMap<>(stage.backing));
    }

    public static <I, R, X extends Exception> XChoices.Stage<I, R, X> add(final Id<I, R, X> id,
                                                                          final Predicate<I> criterion,
                                                                          final Id<I, R, X> positive) {
        return add(id, criterion, positive, positive.not());
    }

    public static <I, R, X extends Exception> XChoices.Stage<I, R, X> add(final Id<I, R, X> id,
                                                                          final Predicate<I> criterion,
                                                                          final Id<I, R, X> positive,
                                                                          final Id<I, R, X> negative) {
        return new XChoices.Stage<>(id).add(id, criterion, positive, negative);
    }

    @Override
    public final R apply(final I input) throws X {
        return map(start, input);
    }

    private XFunction<I, R, X> map(final Predicate<I> criterion, final Id<I, R, X> positive, final Id<I, R, X> negative) {
        return input -> map(criterion.test(input) ? positive : negative, input);
    }

    private R map(final Id<I, R, X> id, final I input) throws X {
        return Optional.ofNullable(backing.get(id))
                       .orElseThrow(IllegalStateException::new)
                       .apply(this)
                       .apply(input);
    }

    public interface Id<I, R, X extends Exception> {

        default Id<I, R, X> not() {
            return new Not<>(this);
        }
    }

    private static final class Not<I, R, X extends Exception> implements Id<I, R, X> {

        private final Id<I, R, X> id;

        private Not(final Id<I, R, X> id) {
            this.id = id;
        }

        @Override
        public final Id<I, R, X> not() {
            return id;
        }

        @Override
        public int hashCode() {
            return ~id.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            return this == obj || (obj instanceof Not && id.equals(((Not<?,?,?>)obj).id));
        }

        @Override
        public final String toString() {
            return "~" + id;
        }
    }

    public static final class Stage<I, R, X extends Exception> {

        private final Map<Id<?, ?, ?>, Function<XChoices<I, R, X>, XFunction<I, R, X>>> backing = new HashMap<>(0);
        private final Id<I, R, X> start;
        private final Set<Object> defined = new HashSet<>(0);
        private final Set<Object> used = new HashSet<>(0);

        private Stage(final Id<I, R, X> start) {
            this.start = start;
            addUsed(start);
        }

        public final Stage<I, R, X> add(final Id<I, R, X> id,
                                        final Predicate<I> criterion,
                                        final Id<I, R, X> positive) {
            return add(id, criterion, positive, positive.not());
        }

        public final Stage<I, R, X> add(final Id<I, R, X> id,
                                        final Predicate<I> criterion,
                                        final Id<I, R, X> positive,
                                        final Id<I, R, X> negative) {
            backing.put(id, choices -> choices.map(criterion, positive, negative));
            return addDefined(id).addUsed(positive, negative);
        }

        @SafeVarargs
        private final Stage<I, R, X> addUsed(final Id<I, R, X>... ids) {
            used.addAll(Arrays.asList(ids));
            return this;
        }

        private Stage<I, R, X> addDefined(final Id<I, R, X> id) {
            defined.add(id);
            return this;
        }

        public final Stage<I, R, X> add(final Id<I, R, X> id,
                                        final XFunction<I, R, X> method) {
            backing.put(id, ignored -> method);
            return addDefined(id);
        }

        public XChoices<I, R, X> orElseThrow(final Supplier<? extends X> supplier) {
            final HashSet<Object> usedButNotDefined = new HashSet<>(used);
            usedButNotDefined.removeAll(defined);
            if (!usedButNotDefined.isEmpty()) {
                throw new UsedButNotDefinedException(usedButNotDefined);
            }

            final HashSet<Object> definedButNotUsed = new HashSet<>(defined);
            definedButNotUsed.removeAll(used);
            if (!definedButNotUsed.isEmpty()) {
                throw new DefinedButNotUsedException(definedButNotUsed);
            }

            return new XChoices<I, R, X>(this);
        }
    }
}
