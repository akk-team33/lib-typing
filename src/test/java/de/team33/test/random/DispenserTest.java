package de.team33.test.random;

import de.team33.libs.typing.v4.Type;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class DispenserTest<T> {

    private static final Logger LOGGER = Logger.getLogger(DispenserTest.class.getCanonicalName());
    private static final Supplier<Dispenser> DISPENSER = Dispenser.builder()
                                                                  .put(Boolean.class, dsp -> dsp.basics.anyBoolean())
                                                                  .put(byte.class, dsp -> dsp.basics.anyByte())
                                                                  .put(Short.class, dsp -> dsp.basics.anyShort())
                                                                  .put(int.class, dsp -> dsp.basics.anyInt())
                                                                  .put(Long.class, dsp -> dsp.basics.anyLong())
                                                                  .put(float.class, dsp -> dsp.basics.anyFloat())
                                                                  .put(Double.class, dsp -> dsp.basics.anyDouble())
                                                                  .put(char.class, dsp -> dsp.basics.anyChar())
                                                                  .setDefaultCharset("abc")
                                                                  .prepare();
    private static final int MAX_LOOP_PRIMITIVES = 1;
    private static final int MAX_LOOP_ARRAYS = 1;

    private final Type<T> type;
    private final Predicate<T> validity;
    private final int maxLoop;

    public DispenserTest(final Type<T> type, final Predicate<T> validity, final int maxLoop) {
        this.type = type;
        this.validity = validity;
        this.maxLoop = maxLoop;
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
                getParameter(Character.class),

                getParameter(Object.class, MAX_LOOP_ARRAYS),
                getParameter(CharSequence.class, MAX_LOOP_ARRAYS),
                getParameter(Serializable.class, MAX_LOOP_ARRAYS),
                getParameter(new Type<Comparable<String>>(){}, String.class::isInstance, MAX_LOOP_ARRAYS),
                getParameter(String.class, MAX_LOOP_ARRAYS),

                getParameter(TimeUnit.class),

                getParameter(boolean[].class, MAX_LOOP_ARRAYS),
                getParameter(byte[].class, MAX_LOOP_ARRAYS),
                getParameter(short[].class, MAX_LOOP_ARRAYS),
                getParameter(int[].class, MAX_LOOP_ARRAYS),
                getParameter(long[].class, MAX_LOOP_ARRAYS),
                getParameter(float[].class, MAX_LOOP_ARRAYS),
                getParameter(double[].class, MAX_LOOP_ARRAYS),
                getParameter(char[].class, MAX_LOOP_ARRAYS),

                getParameter(Boolean[].class, MAX_LOOP_ARRAYS),
                getParameter(Byte[].class, MAX_LOOP_ARRAYS),
                getParameter(Short[].class, MAX_LOOP_ARRAYS),
                getParameter(Integer[].class, MAX_LOOP_ARRAYS),
                getParameter(Long[].class, MAX_LOOP_ARRAYS),
                getParameter(Float[].class, MAX_LOOP_ARRAYS),
                getParameter(Double[].class, MAX_LOOP_ARRAYS),
                getParameter(Character[].class, MAX_LOOP_ARRAYS),

                getParameter(String[].class, MAX_LOOP_ARRAYS)
        );
    }

    private static <T> Object[] getParameter(final Class<T> type) {
        return getParameter(type, type);
    }

    private static <T> Object[] getParameter(final Class<T> type, final Class<T> expected) {
        return getParameter(type, expected::isInstance, MAX_LOOP_PRIMITIVES);
    }

    private static <T> Object[] getParameter(final Class<T> type, final int maxLoop) {
        return getParameter(type, type::isInstance, maxLoop);
    }

    private static <T> Object[] getParameter(final Class<T> type, final Predicate<? super T> validity, final int maxLoop) {
        return getParameter(Type.of(type), validity, maxLoop);
    }

    private static <T> Object[] getParameter(final Type<T> type, final Predicate<? super T> validity, final int maxLoop) {
        return new Object[]{type, validity, maxLoop};
    }

    @Test
    public final void any() {
        for (int i = 0; i < maxLoop; ++i) {
            final T result = DISPENSER.get().any(type);
            log(i, result);
            assertTrue("" + type, validity.test(result));
        }
    }

    private void log(final int index, final T result) {
        if (result.getClass().isArray())
            LOGGER.info(() -> String.format("%d. %s -> Result[%d]: %s", index, type, Array.getLength(result), arrayToString(result)));
        else
            LOGGER.info(() -> String.format("%d. %s -> Result: %s", index, type, result));
    }

    private String arrayToString(final Object array) {
        final int length = Array.getLength(array);
        final List<Object> result = new ArrayList<>(length);
        for (int i = 0; i < length; ++i)
            result.add(Array.get(array, i));
        return result.toString();
    }
}
