package de.team33.test.typing.v4.experimental3;

import org.junit.Test;

import java.util.Random;

import static de.team33.libs.testing.v1.Attempts.trySerial;
import static org.junit.Assert.assertEquals;

public class CasesTest {

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
}
