package de.team33.test.typing.v4.experimental3;

import de.team33.libs.typing.v4.experimental2.Case;
import de.team33.libs.typing.v4.experimental2.Cases;

import java.util.function.Function;
import java.util.function.Predicate;

enum DynamicChoices implements Case<Input, String> {

    CASE_____(null, null),
    CASE_0___(input -> 0 == input.a, null),
    CASE_00__(input -> 0 == input.b, null),
    CASE_000_(input -> 0 == input.c, null),
    CASE_0000(input -> 0 == input.d, Input::toString),
    CASE_0001(null, Input::toString),
    CASE_001_(null, null),
    CASE_0010(input -> 0 == input.d, Input::toString),
    CASE_0011(null, Input::toString),
    CASE_01__(null, null),
    CASE_010_(input -> 0 == input.c, null),
    CASE_0100(input -> 0 == input.d, Input::toString),
    CASE_0101(null, Input::toString),
    CASE_011_(null, null),
    CASE_0110(input -> 0 == input.d, Input::toString),
    CASE_0111(null, Input::toString),
    CASE_1___(null, null),
    CASE_10__(input -> 0 == input.b, null),
    CASE_100_(input -> 0 == input.c, null),
    CASE_1000(input -> 0 == input.d, Input::toString),
    CASE_1001(null, Input::toString),
    CASE_101_(null, null),
    CASE_1010(input -> 0 == input.d, Input::toString),
    CASE_1011(null, Input::toString),
    CASE_11__(null, null),
    CASE_110_(input -> 0 == input.c, null),
    CASE_1100(input -> 0 == input.d, Input::toString),
    CASE_1101(null, Input::toString),
    CASE_111_(null, null),
    CASE_1110(input -> 0 == input.d, Input::toString),
    CASE_1111(null, Input::toString);

    private final Predicate<Input> predicate;
    private final Function<Input, String> function;

    private static final Cases<Input, String> CASES = Cases
            .on(CASE_____).when(CASE_0___.predicate).then(CASE_0___).orElse(CASE_1___)

            .on(CASE_0___).when(CASE_00__.predicate).then(CASE_00__).orElse(CASE_01__)
            .on(CASE_1___).when(CASE_10__.predicate).then(CASE_10__).orElse(CASE_11__)

            .on(CASE_00__).when(CASE_000_.predicate).then(CASE_000_).orElse(CASE_001_)
            .on(CASE_01__).when(CASE_010_.predicate).then(CASE_010_).orElse(CASE_011_)
            .on(CASE_10__).when(CASE_100_.predicate).then(CASE_100_).orElse(CASE_101_)
            .on(CASE_11__).when(CASE_110_.predicate).then(CASE_110_).orElse(CASE_111_)

            .on(CASE_000_).when(CASE_0000.predicate).then(CASE_0000).orElse(CASE_0001)
            .on(CASE_001_).when(CASE_0010.predicate).then(CASE_0010).orElse(CASE_0011)
            .on(CASE_010_).when(CASE_0100.predicate).then(CASE_0100).orElse(CASE_0101)
            .on(CASE_011_).when(CASE_0110.predicate).then(CASE_0110).orElse(CASE_0111)
            .on(CASE_100_).when(CASE_1000.predicate).then(CASE_1000).orElse(CASE_1001)
            .on(CASE_101_).when(CASE_1010.predicate).then(CASE_1010).orElse(CASE_1011)
            .on(CASE_110_).when(CASE_1100.predicate).then(CASE_1100).orElse(CASE_1101)
            .on(CASE_111_).when(CASE_1110.predicate).then(CASE_1110).orElse(CASE_1111)

            .on(CASE_0000).apply(CASE_0000.function)
            .on(CASE_0001).apply(CASE_0001.function)
            .on(CASE_0010).apply(CASE_0010.function)
            .on(CASE_0011).apply(CASE_0011.function)
            .on(CASE_0100).apply(CASE_0100.function)
            .on(CASE_0101).apply(CASE_0101.function)
            .on(CASE_0110).apply(CASE_0110.function)
            .on(CASE_0111).apply(CASE_0111.function)
            .on(CASE_1000).apply(CASE_1000.function)
            .on(CASE_1001).apply(CASE_1001.function)
            .on(CASE_1010).apply(CASE_1010.function)
            .on(CASE_1011).apply(CASE_1011.function)
            .on(CASE_1100).apply(CASE_1100.function)
            .on(CASE_1101).apply(CASE_1101.function)
            .on(CASE_1110).apply(CASE_1110.function)
            .on(CASE_1111).apply(CASE_1111.function)

            .build();

    DynamicChoices(final Predicate<Input> predicate, final Function<Input, String> function) {
        this.predicate = predicate;
        this.function = function;
    }

    static String map(final Input input) {
        return CASES.apply(input);
    }
}
