package de.team33.test.typing.v3;

import de.team33.libs.typing.v3.Key;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KeyTest {

    private static final Key<String> KEY = new Key<>();

    @Test
    public final void to_String() {
        assertEquals("de.team33.test.typing.v3.KeyTest.<clinit>(KeyTest.java:10)", KEY.toString());
    }
}