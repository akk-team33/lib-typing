package de.team33.test.typing.v4.experimental3;

import de.team33.libs.typing.v4.experimental3.Case;
import de.team33.libs.typing.v4.experimental3.Cases;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

enum DynamicChoices implements Case<Input, String> {

    CASE_0___(input -> 0 == input.a),
    CASE_00__(input -> 0 == input.b),
    CASE_000_(input -> 0 == input.c),

    CASE_1_0_(input -> 0 == input.c),
    CASE_101_(input -> 0 == input.b),
    CASE_010_(input -> 0 == input.c),

    CASE_0000(input -> 0 == input.d, "0000", "0001"),
    CASE_0010(input -> 0 == input.d, "0010", "0011"),
    CASE_0100(input -> 0 == input.d, "0100", "0101"),
    CASE_1_00(input -> 0 == input.d, "1_00", "1_01"),
    CASE_0110(input -> 0 == input.d, "0110", "0111"),
    CASE_1010(input -> 0 == input.d, "1010", "1011"),
    CASE_1110(input -> 0 == input.d, "1110", "1111");

    private static final Cases<Input, String> CASES = Cases
            .checkAll(CASE_0___, CASE_00__)

            .when(CASE_00__).check(CASE_000_)
            .when(CASE_000_).check(CASE_0000)

            .whenNot(CASE_0___).checkAll(CASE_1_0_, CASE_1_00)
            .whenNot(CASE_1_0_).checkAll(CASE_101_, CASE_1010)
            .whenNot(CASE_00__).checkAll(CASE_010_, CASE_0100)

            .whenNot(CASE_000_).check(CASE_0010)
            .whenNot(CASE_101_).check(CASE_1110)
            .whenNot(CASE_010_).check(CASE_0110)

            .build();

    private final Predicate<? super Input> predicate;
    private final Function<Input, String> positive;
    private final Function<Input, String> negative;

    DynamicChoices(final Predicate<? super Input> predicate) {
        this(predicate, null, null);
    }

    @SuppressWarnings("AssignmentToNull")
    DynamicChoices(final Predicate<? super Input> predicate,
                   final String positive,
                   final String negative) {
        this.predicate = predicate;
        this.positive = null == positive ? null : x -> positive;
        this.negative = null == negative ? null : x -> negative;
    }

    static String map(final Input input) {
        return CASES.apply(input);
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
