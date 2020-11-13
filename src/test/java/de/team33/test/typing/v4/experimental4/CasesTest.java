package de.team33.test.typing.v4.experimental4;

import de.team33.libs.typing.v4.experimental4.Case;
import de.team33.libs.typing.v4.experimental4.Cases;
import org.junit.Test;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.team33.libs.testing.v1.Attempts.tryParallel;
import static de.team33.libs.testing.v1.Attempts.trySerial;
import static org.junit.Assert.assertEquals;

public class CasesTest {

    private static final Case<Integer, Function<Integer, String>> POSITIVE;

    private static final Case<Integer, Function<Integer, String>> NEGATIVE;

    static {
        POSITIVE = new Case<Integer, Function<Integer, String>>() {

            @Override
            public Case<Integer, Function<Integer, String>> getPreCondition() {
                //return null;
                return Case.none();
                //return Case.not(NEGATIVE);
            }

            @Override
            public Optional<Predicate<Integer>> getCondition() {
                return Optional.of(input -> input > 0);
            }

            @Override
            public Optional<Function<Integer, String>> getResult() {
                return Optional.of(String::valueOf);
            }

            @Override
            public String toString() {
                return "POSITIVE";
            }
        };
        NEGATIVE = new Case<Integer, Function<Integer, String>>() {

            @Override
            public Case<Integer, Function<Integer, String>> getPreCondition() {
                //return null;
                //return Case.none();
                return Case.not(POSITIVE);
            }

            @Override
            public Optional<Predicate<Integer>> getCondition() {
                //return Optional.of(input -> input < 0);
                return Optional.empty();
            }

            @Override
            public Optional<Function<Integer, String>> getResult() {
                return Optional.of(input -> String.valueOf(-input));
                //return Optional.empty();
            }

            @Override
            public String toString() {
                return "NEGATIVE";
            }
        };
    }

    private final Random random = new Random();

    @Test
    public final void fixed_vs_intern() {
        trySerial(100, () -> {
            final Input input = new Input(random.nextInt());
            assertEquals(input.toString(), StaticChoices.map(input));
        });
    }

    @Test
    public final void fixed_vs_variable() {
        trySerial(100, () -> {
            final Input input = new Input(random.nextInt());
            assertEquals(StaticChoices.map(input), DynamicChoices.map(input));
        });
    }

    @Test
    public final void checkDualParallel() {
        final Cases<Integer, Function<Integer, String>> cases = Cases.build(POSITIVE, NEGATIVE);
        tryParallel(100, () -> {
            final int input = random.nextInt();
            final String result = cases.apply(input).apply(input);
            assertEquals(String.valueOf(Math.abs(input)), result);
        });
    }
}
