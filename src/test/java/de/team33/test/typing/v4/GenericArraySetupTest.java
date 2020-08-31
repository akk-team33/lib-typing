package de.team33.test.typing.v4;

import de.team33.libs.typing.v4.Type;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertSame;

public class GenericArraySetupTest {

    private static final Type<Map<String, List<Set<String>>>[]> SAMPLE_TYPE = new Type<Map<String, List<Set<String>>>[]>() {};

    @SuppressWarnings("unchecked")
    private final Map<String, List<Set<String>>>[] sample = new Map[0];

    @Test
    public final void getPrimeClass() {
        assertSame(SAMPLE_TYPE.getPrimeClass(), sample.getClass());
    }
}
