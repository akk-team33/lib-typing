package de.team33.test.random;

import de.team33.libs.typing.v4.Type;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DispenserTest<T> {

    private static final Supplier<Dispenser> DISPENSER = Dispenser.builder()
                                                                  .put(Boolean.class, Dispenser::anyBoolean)
                                                                  .put(byte.class, Dispenser::anyByte)
                                                                  .put(Short.class, Dispenser::anyShort)
                                                                  .put(int.class, Dispenser::anyInt)
                                                                  .put(Long.class, Dispenser::anyLong)
                                                                  .put(float.class, Dispenser::anyFloat)
                                                                  .put(Double.class, Dispenser::anyDouble)
                                                                  .put(char.class, Dispenser::anyChar)
                                                                  .prepare();

    private final Type<T> type;
    private final Predicate<T> validity;

    public DispenserTest(final Type<T> type, final Predicate<T> validity) {
        this.type = type;
        this.validity = validity;
    }

    @Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList(
                getParameter(boolean.class, Boolean.class),
                getParameter(Boolean.class),
                getParameter(byte.class, Byte.class),
                getParameter(Byte.class),
                getParameter(short.class, Short.class),
                getParameter(Short.class),
                getParameter(int.class, Integer.class),
                getParameter(Integer.class),
                getParameter(long.class, Long.class),
                getParameter(Long.class),
                getParameter(float.class, Float.class),
                getParameter(Float.class),
                getParameter(double.class, Double.class),
                getParameter(Double.class),
                getParameter(char.class, Character.class),
                getParameter(Character.class)
        );
    }

    private static <T> Object[] getParameter(final Class<T> type) {
        return getParameter(type, type);
    }

    private static <T> Object[] getParameter(final Class<T> type, final Class<T> expected) {
        return getParameter(type, expected::isInstance);
    }

    private static <T> Object[] getParameter(final Class<T> type, final Predicate<T> validity) {
        return getParameter(Type.of(type), validity);
    }

    private static <T> Object[] getParameter(final Type<T> type, final Predicate<T> validity) {
        return new Object[]{type, validity};
    }

    @Test
    public final void any10000() {
        for (int i = 0; i < 10000; ++i) {
            final T result = DISPENSER.get().any(type);
            assertTrue("" + type, validity.test(result));
        }
    }
}
