package de.team33.test.typing.v4.experimental3;

import de.team33.libs.typing.v4.experimental3.Case;
import de.team33.libs.typing.v4.experimental3.Cases;
import org.junit.Test;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import static de.team33.libs.testing.v1.Attempts.trySerial;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CasesTest {

    private static final Case<Integer, String> SINGLE = new Case<Integer, String>() {
        @Override
        public boolean isMatching(final Integer input) {
            return input > 0;
        }

        @Override
        public Optional<Function<Integer, String>> getPositive() {
            return Optional.of(input -> "" + input);
        }

        @Override
        public Optional<Function<Integer, String>> getNegative() {
            return Optional.of(input -> "" + (-input));
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
    public final void checkSingle() {
        final Cases<Integer, String> cases = Cases.check(SINGLE).build();
        trySerial(100, () -> {
            final int input = random.nextInt();
            final String result = cases.apply(input);
            assertEquals("" + Math.abs(input), result);
        });
    }

    @Test
    public final void fixed_vs_variable() {
        trySerial(100, () -> {
            final Input input = new Input(random.nextInt());
            assertEquals(StaticChoices.map(input), DynamicChoices.map(input));
        });
    }
}
