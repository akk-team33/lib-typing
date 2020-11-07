package de.team33.test.typing.v4.experimental3;

import de.team33.libs.typing.v4.experimental3.Case;
import de.team33.libs.typing.v4.experimental3.Cases;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.team33.libs.typing.v4.experimental3.Case.*;

enum DynamicChoices implements Case<Input, String> {

    CASE_0___(input -> 0 == input.a),
    CASE_00__(input -> 0 == input.b),
    CASE_000_(input -> 0 == input.c),
    CASE_0000(input -> 0 == input.d, "0000", "0001"),
    CASE_0010(input -> 0 == input.d, "0010", "0011"),
    CASE_010_(input -> 0 == input.c),
    CASE_0100(input -> 0 == input.d, "0100", "0101"),
    CASE_0110(input -> 0 == input.d, "0110", "0111"),
    CASE_10__(input -> 0 == input.b),
    CASE_100_(input -> 0 == input.c),
    CASE_1000(input -> 0 == input.d, "1000", "1001"),
    CASE_1010(input -> 0 == input.d, "1010", "1011"),
    CASE_110_(input -> 0 == input.c),
    CASE_1100(input -> 0 == input.d, "1100", "1101"),
    CASE_1110(input -> 0 == input.d, "1110", "1111");

    private static final Cases<Input, String> CASES = Cases
            .check(CASE_0___)

            .on(CASE_0___).check(CASE_00__)
            .on(not(CASE_0___)).check(CASE_10__)

            .on(CASE_00__).check(CASE_000_)
            .on(not(CASE_00__)).check(CASE_010_)
            .on(CASE_10__).check(CASE_100_)
            .on(not(CASE_10__)).check(CASE_110_)

            .on(CASE_000_).check(CASE_0000)
            .on(not(CASE_000_)).check(CASE_0010)
            .on(CASE_010_).check(CASE_0100)
            .on(not(CASE_010_)).check(CASE_0110)
            .on(CASE_100_).check(CASE_1000)
            .on(not(CASE_100_)).check(CASE_1010)
            .on(CASE_110_).check(CASE_1100)
            .on(not(CASE_110_)).check(CASE_1110)

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
        this.positive = (null == positive) ? null : x -> positive;
        this.negative = (null == negative) ? null : x -> negative;
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
