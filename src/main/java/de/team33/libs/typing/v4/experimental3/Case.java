package de.team33.libs.typing.v4.experimental3;

import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a case that can lead to the solution of a function or a further case distinction.
 *
 * @param <I> The type of function parameters to be expected
 * @param <R> The type of function result to be expected
 */
public interface Case<I, R> {

    /**
     * Returns the opposite of a given {@link Case}.
     */
    static <I, R> Case<I, R> not(final Case<I, R> original) {
        return Opposite.of(original);
    }

    /**
     * Checks whether this {@link Case} applies based on a given parameter.
     * <p>
     * Note 1: if not, it means its {@link #not(Case) opposite} is occurring.
     * <p>
     * Note 2: Certain requirements may have to be met in order for this test to be meaningful.
     * In particular, there may be higher-level cases, the occurrence of which must first be clarified.
     * <p>
     * Note 3: The same requirements apply to a specific case and its opposite!
     */
    boolean isMatching(I input);

    /**
     * Returns a function that can deliver the final result for a certain parameter if this case
     * {@link #isMatching(Object) applies} to the parameter and no further case distinction is necessary.
     */
    Optional<Function<I,R>> getPositive();

    /**
     * Returns a function that can deliver the final result for a certain parameter if this case does not
     * {@link #isMatching(Object) apply} to the parameter and no further case distinction is necessary.
     */
    Optional<Function<I,R>> getNegative();
}
