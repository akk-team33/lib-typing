package de.team33.libs.typing.v4.experimental4;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a case that can lead to an end result of a function or a further case distinction.
 *
 * @param <I> The type of function parameters to be expected
 * @param <R> The type of function result to be expected
 */
public interface Case<I, R> {

    /**
     * Returns the opposite of a given {@link Case}.
     * It has the following properties:
     * <ul>
     *     <li>it has the same precondition as the original</li>
     *     <li>it has the inverse condition of the original
     *     (if the original has no condition it makes no sense to get its opposite)</li>
     *     <li>it never leads directly to a result</li>
     * </ul>
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
     * In order to {@link #getCondition() clarify whether a certain case applies}, its precondition must apply.
     * <p>
     * Within a decision chain or a decision tree, exactly one case typically has no real precondition.
     * Such a case should return the pseudo-case {@link #none()}.
     */
    Case<I, R> getPreCondition();

    /**
     * Provides {@link Optional (indirectly)} a {@link Predicate condition} that (in addition to the
     * {@link #getPreCondition() precondition}) must be fulfilled for this {@link Case} to apply if such a condition
     * exists. This implies the {@link #not(Case) opposite case}, in which the same precondition applies but this
     * condition does exactly not apply.
     * <p>
     * If no such condition exists (i.e. the result is {@link Optional#empty()}), this means that only the
     * {@link #getPreCondition() precondition} must be fulfilled for this case to apply. This fact does not imply an
     * opposite case (or an {@link #not(Case) opposite case} that can never apply).
     */
    Optional<Predicate<I>> getCondition();

    /**
     * Provides {@link Optional (indirectly)} a {@link Function method} that can deliver the final result for the
     * appliance of this case, if no further case distinction is necessary.
     * <p>
     * Results in {@link Optional#empty()} if at least one further case distinction has to be made in order to arrive
     * at the final result.
     */
    Optional<Function<I, R>> getMethod();
}
