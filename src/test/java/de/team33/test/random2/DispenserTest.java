package de.team33.test.random2;

import de.team33.libs.typing.v4.Type;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

@RunWith(Parameterized.class)
public class DispenserTest<T> {

    private static final Logger LOGGER = Logger.getLogger(DispenserTest.class.getCanonicalName());
    private static final Supplier<Dispenser> DISPENSER = Dispenser.builder()
                                                                  .putMethod(int.class, dsp -> 277)
                                                                  .putMethod(Integer.class, dsp -> 278)
                                                                  .prepare();

    private final Dispenser subject = DISPENSER.get();
    private final Alt<Type<T>, Class<T>> alt;
    private final int maxLoop;
    private final Predicate<Object> validity;

    public DispenserTest(final Alt<Type<T>, Class<T>> alt, final int maxLoop, final Predicate<Object> validity) {
        this.alt = alt;
        this.maxLoop = maxLoop;
        this.validity = validity;
    }

    private static <T> Object[] getParameters(final Class<T> tClass, final int maxLoop, final Predicate<Object> validity) {
        return new Object[]{Alt.right(tClass), maxLoop, validity};
    }

    private static <T> Object[] getParameters(final Type<T> tType, final int maxLoop, final Predicate<Object> validity) {
        return new Object[]{Alt.left(tType), maxLoop, validity};
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList(
                getParameters(int.class, 100, DispenserTest::isInteger),
                getParameters(Type.of(Integer.class), 100, DispenserTest::isInteger)
        );
    }

    private static boolean isInteger(final Object sample) {
        return sample instanceof Integer;
    }

    private void log(final int index, final T result) {
        if (null != result && result.getClass().isArray()) {
            LOGGER.info(() -> String.format("%d. %s -> Result[%d]: %s", index, alt, Array.getLength(result), arrayToString(result)));
        } else {
            LOGGER.info(() -> String.format("%d. %s -> Result: %s", index, alt, result));
        }
    }

    private String arrayToString(final Object array) {
        final int length = Array.getLength(array);
        final List<Object> result = new ArrayList<>(length);
        for (int i = 0; i < length; ++i) {
            result.add(Array.get(array, i));
        }
        return result.toString();
    }

    @Test
    public final void any() {
        for (int i = 0; i < maxLoop; ++i) {
            final T result = alt.apply(subject::any, subject::any);
            log(i, result);
            Assert.assertTrue(validity.test(result));
        }
    }
}
