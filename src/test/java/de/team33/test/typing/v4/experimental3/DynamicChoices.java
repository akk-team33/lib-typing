package de.team33.test.typing.v4.experimental3;

import de.team33.libs.typing.v4.experimental3.Case;
import de.team33.libs.typing.v4.experimental3.Cases;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.team33.libs.typing.v4.experimental3.Case.not;

enum DynamicChoices implements Case<Input, String> {

    CASE_0___(null, input -> 0 == input.a),
    CASE_00__(CASE_0___, input -> 0 == input.b),
    CASE_000_(CASE_00__, input -> 0 == input.c),

    CASE_1_0_(not(CASE_0___), input -> 0 == input.c),
    CASE_101_(not(CASE_1_0_), input -> 0 == input.b),
    CASE_010_(not(CASE_00__), input -> 0 == input.c),

    CASE_0000(CASE_000_, input -> 0 == input.d, "0000", "0001"),
    CASE_0100(CASE_010_, input -> 0 == input.d, "0100", "0101"),
    CASE_1_00(CASE_1_0_, input -> 0 == input.d, "1_00", "1_01"),
    CASE_1010(CASE_101_, input -> 0 == input.d, "1010", "1011"),

    CASE_0010(not(CASE_000_), input -> 0 == input.d, "0010", "0011"),
    CASE_0110(not(CASE_010_), input -> 0 == input.d, "0110", "0111"),
    CASE_1110(not(CASE_101_), input -> 0 == input.d, "1110", "1111");

    private static final Cases<Input, String> CASES = Cases.build(values());

    private final Case<Input, String> preCondition;
    private final Predicate<? super Input> predicate;
    private final Function<Input, String> positive;
    private final Function<Input, String> negative;

    DynamicChoices(final Case<Input, String> preCondition, final Predicate<? super Input> predicate) {
        this(preCondition, predicate, null, null);
    }

    @SuppressWarnings("AssignmentToNull")
    DynamicChoices(final Case<Input, String> preCondition,
                   final Predicate<? super Input> predicate,
                   final String positive,
                   final String negative) {
        this.preCondition = preCondition;
        this.predicate = predicate;
        this.positive = null == positive ? null : x -> positive;
        this.negative = null == negative ? null : x -> negative;
    }

    static String map(final Input input) {
        return CASES.apply(input);
    }

    public Optional<Case<Input, String>> getPreCondition() {
        return Optional.ofNullable(preCondition);
    }

    @Override
    public boolean isMatching(final Input input) {
        return predicate.test(input);
    }

    @Override
    public Optional<Function<Input, String>> getPositive() {
        return Optional.ofNullable(positive);
    }

    @Override
    public Optional<Function<Input, String>> getNegative() {
        return Optional.ofNullable(negative);
    }
}
