package de.team33.test.typing.v4.experimental4;

import de.team33.libs.typing.v4.experimental4.Case;
import de.team33.libs.typing.v4.experimental4.Cases;
import de.team33.test.typing.v4.experimental3.Input;

import java.util.function.Function;
import java.util.function.Predicate;

import static de.team33.libs.typing.v4.experimental4.Case.none;
import static de.team33.libs.typing.v4.experimental4.Case.not;

enum DynamicChoices implements Case<Input, String> {

    CASE_0___(none(), input -> 0 == input.a),
    CASE_00__(CASE_0___, input -> 0 == input.b),
    CASE_000_(CASE_00__, input -> 0 == input.c),

    CASE_1_0_(not(CASE_0___), input -> 0 == input.c),
    CASE_101_(not(CASE_1_0_), input -> 0 == input.b),
    CASE_010_(not(CASE_00__), input -> 0 == input.c),

    CASE_0000(CASE_000_, input -> 0 == input.d, "0000"),
    CASE_0001(not(CASE_0000), null, "0001"),
    CASE_0100(CASE_010_, input -> 0 == input.d, "0100"),
    CASE_0101(not(CASE_0100), null, "0101"),
    CASE_1_00(CASE_1_0_, input -> 0 == input.d, "1_00"),
    CASE_1_01(not(CASE_1_00), null, "1_01"),
    CASE_1010(CASE_101_, input -> 0 == input.d, "1010"),
    CASE_1011(not(CASE_1010), null, "1011"),

    CASE_0010(not(CASE_000_), input -> 0 == input.d, "0010"),
    CASE_0011(not(CASE_0010), null, "0011"),
    CASE_0110(not(CASE_010_), input -> 0 == input.d, "0110"),
    CASE_0111(not(CASE_0110), null, "0111"),
    CASE_1110(not(CASE_101_), input -> 0 == input.d, "1110"),
    CASE_1111(not(CASE_1110), null, "1111");

    private static final Cases<Input, String> CASES = Cases.build(values());
    private static final Predicate<Input> TRUE = input -> true;

    private final Case<Input, String> preCondition;
    private final Predicate<Input> predicate;
    private final Function<Input, String> method;

    DynamicChoices(final Case<Input, String> preCondition, final Predicate<Input> predicate) {
        this(preCondition, predicate, null);
    }

    @SuppressWarnings("AssignmentToNull")
    DynamicChoices(final Case<Input, String> preCondition,
                   final Predicate<Input> predicate,
                   final String result) {
        this.preCondition = preCondition;
        this.predicate = predicate;
        this.method = (null == result) ? null : x -> result;
    }

    static String map(final Input input) {
        return CASES.apply(input);
    }

    public final Case<Input, String> getPreCondition() {
        return preCondition;
    }

    @Override
    public final boolean isDefault() {
        return null == predicate;
    }

    @Override
    public final boolean isMatching(final Input input) {
        return ((null == predicate) ? TRUE : predicate).test(input);
    }

    @Override
    public final boolean isDefinite() {
        return null != method;
    }

    @Override
    public final String apply(final Input input) {
        return method.apply(input);
    }
}
