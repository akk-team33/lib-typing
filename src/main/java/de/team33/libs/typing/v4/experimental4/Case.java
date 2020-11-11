package de.team33.libs.typing.v4.experimental4;

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
     * <p>
     * The opposite of a {@link Case} is not a negation in the strictly boolean sense.
     * In fact, the opposite of a case has the same precondition as the original case.
     * Only the case-specific condition is negated.
     */
    static <I, R> Case<I, R> not(final Case<I, R> original) {
        return Opposite.of(original);
    }

    /**
     * Returns the {@link Case} that symbolizes that no specific case has yet been clarified.
     * It has the following properties:
     *
     * <ul>
     *     <li>it is itself its own precondition</li>
     *     <li>it is always fulfilled</li>
     *     <li>it never leads directly to a result</li>
     * </ul>
     */
    static <I, R> Case<I, R> none() {
        return None.instance();
    }

    /**
     * Returns the precondition for this {@link Case}.
     * <p>
     * In order to {@link #isMatching(Object) clarify whether a certain case is met}, its precondition must be met.
     * <p>
     * Within a decision chain or a decision tree, exactly one case typically has no real precondition.
     * Such a case should return the pseudo-case {@link #none()}.
     */
    Case<I, R> getPreCondition();

    boolean isDefault();

    /**
     * Checks whether this {@link Case} applies based on a given parameter.
     * <p>
     * Note 1: if not, it means its {@link #not(Case) opposite} applies.
     * <p>
     * Note 2: Certain requirements may have to be met in order for this test to be meaningful.
     * In particular, there may be higher-level cases, the occurrence of which must first be clarified.
     * <p>
     * Note 3: The same requirements apply to a specific case and its opposite!
     */
    boolean isMatching(I input);

    boolean isDefinite();

    R apply(I input);
}
