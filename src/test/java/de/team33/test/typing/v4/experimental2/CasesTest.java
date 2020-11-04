package de.team33.test.typing.v4.experimental2;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Date;

import static org.junit.Assert.*;

public class CasesTest {

    @Test
    public final void apply_INTEGER() {
        assertEquals(Integer.class, Dispenser.any(Integer.class).getClass());
    }

    @Test
    public final void apply_BIG_INTEGER() {
        assertEquals(BigInteger.class, Dispenser.any(BigInteger.class).getClass());
    }

    @Test
    public final void apply_STRING() {
        assertEquals(String.class, Dispenser.any(String.class).getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void apply_DATE() {
        fail("Expected to fail with <IllegalArgumentException> but was: " + Dispenser.any(Date.class));
    }
}
