package de.team33.test.typing.v4.experimental3;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

public class DispenserTest {

    private final Random random = new Random();

    @Test
    public final void apply_Integer() {
        final Object result = Dispenser.apply(Integer.class, random);
        assertEquals(Integer.class, result.getClass());
    }

    @Test
    public final void apply_String() {
        final Object result = Dispenser.apply(String.class, random);
        assertEquals(String.class, result.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void apply_Date() {
        fail("Expected to fail but was " + Dispenser.apply(Date.class, random));
    }
}
