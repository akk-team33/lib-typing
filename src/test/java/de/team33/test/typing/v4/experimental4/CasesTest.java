package de.team33.test.typing.v4.experimental4;

import de.team33.libs.typing.v4.experimental4.Case;
import de.team33.libs.typing.v4.experimental4.Cases;
import de.team33.test.typing.v4.experimental3.Input;
import org.junit.Test;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.team33.libs.testing.v1.Attempts.tryParallel;
import static de.team33.libs.testing.v1.Attempts.trySerial;
import static org.junit.Assert.assertEquals;

public class CasesTest {

    private static final Case<Integer, String> POSITIVE = new Case<Integer, String>() {

        @Override
        public Case<Integer, String> getPreCondition() {
            return Case.none();
        }

        @Override
        public Optional<Predicate<Integer>> getCondition() {
            return Optional.of(input -> input > 0);
        }

        @Override
        public Optional<Function<Integer, String>> getMethod() {
            return Optional.of(String::valueOf);
        }

        @Override
        public String toString() {
            return "POSITIVE";
        }
    };

    private static final Case<Integer, String> NEGATIVE = new Case<Integer, String>() {

        @Override
        public Case<Integer, String> getPreCondition() {
            return Case.not(POSITIVE);
        }

        @Override
        public Optional<Predicate<Integer>> getCondition() {
            return Optional.empty();
        }

        @Override
        public Optional<Function<Integer, String>> getMethod() {
            return Optional.of(input -> String.valueOf(-input));
        }

        @Override
        public String toString() {
            return "NEGATIVE";
        }
    };

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
        final Cases<Integer, String> cases = Cases.build(POSITIVE, NEGATIVE);
        tryParallel(100, () -> {
            final int input = random.nextInt();
            final String result = cases.apply(input);
            assertEquals(String.valueOf(Math.abs(input)), result);
        });
    }
}
