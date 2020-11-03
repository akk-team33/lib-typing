package de.team33.test.typing.v4.experimental;

import de.team33.libs.typing.v4.experimental.XChoices;
import de.team33.libs.typing.v4.experimental.XFunction;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class XChoicesTest {

    @Test
    public final void apply_Integer() {
        final Object result = MyChoices.map(Integer.class);
        assertEquals(Integer.class, result.getClass());
    }

    @Test
    public final void apply_String() {
        final Object result = MyChoices.map(String.class);
        assertEquals(String.class, result.getClass());
    }

    @Test
    public final void apply_BigInteger() {
        final Object result = MyChoices.map(BigInteger.class);
        assertEquals(BigInteger.class, result.getClass());
    }

    @Test(expected = MyException.class)
    public final void apply_Date() {
        fail("Expected to fail but was" + MyChoices.map(Date.class));
    }

    private enum MyChoices implements XChoices.Id<Class<?>, Object, RuntimeException> {

        ANY(null, null),
        INTEGER(Integer.class::equals, MyChoices::nextInt),
        BIG_INTEGER(BigInteger.class::equals, MyChoices::nextBigInteger),
        STRING(String.class::equals, MyChoices::nextString);

        private static final Random random = new Random();
        private static XChoices<Class<?>, Object, RuntimeException> choices = XChoices
                .add(ANY, INTEGER.match, INTEGER)
                .add(INTEGER, INTEGER.method)
                .add(INTEGER.not(), STRING.match, STRING, STRING.not())
                .add(STRING, STRING.method)
                .add(STRING.not(), BIG_INTEGER.match, BIG_INTEGER)
                .add(BIG_INTEGER, BIG_INTEGER.method)
                .add(BIG_INTEGER.not(), MyException::new)
                .orElseThrow(IllegalArgumentException::new);
        private final Predicate<Class<?>> match;
        private final XFunction<Class<?>, Object, RuntimeException> method;

        MyChoices(final Predicate<Class<?>> match, final XFunction<Class<?>, Object, RuntimeException> method) {
            this.match = match;
            this.method = method;
        }

        private static BigInteger nextBigInteger(final Class<?> aClass) {
            return new BigInteger(64 + random.nextInt(64), random);
        }

        private static String nextString(final Class<?> ignore) {
            return nextBigInteger(ignore).toString(Character.MAX_RADIX);
        }

        private static Integer nextInt(final Class<?> ignore) {
            return random.nextInt();
        }

        public static Object map(final Class<?> aClass) {

            return choices.apply(aClass);
        }
    }

    private static final class MyException extends RuntimeException {

        private MyException(final Class<?> aClass) {
            super("No method found for " + aClass);
            throw this;
        }
    }
}
