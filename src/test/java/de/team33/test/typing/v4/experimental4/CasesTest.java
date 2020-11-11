package de.team33.test.typing.v4.experimental4;

import de.team33.libs.typing.v4.experimental4.Case;
import de.team33.libs.typing.v4.experimental4.Cases;
import de.team33.test.typing.v4.experimental3.Input;
import org.junit.Test;

import java.util.Random;

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
        public boolean isDefault() {
            return false;
        }

        @Override
        public boolean isMatching(final Integer input) {
            return input > 0;
        }

        @Override
        public boolean isDefinite() {
            return true;
        }

        @Override
        public String apply(final Integer input) {
            return String.valueOf(input);
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
        public boolean isDefault() {
            return true;
        }

        @Override
        public boolean isMatching(final Integer input) {
            return true;
        }

        @Override
        public boolean isDefinite() {
            return true;
        }

        @Override
        public String apply(final Integer input) {
            return String.valueOf(-input);
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
