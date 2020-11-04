package de.team33.test.typing.v4.experimental2;

import de.team33.libs.typing.v4.experimental2.Case;
import de.team33.libs.typing.v4.experimental2.Cases;

import java.math.BigInteger;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

enum Dispenser implements Case<Class<?>, Object> {

    AMBIGUOUS(null, null),
    INTEGER(Integer.class::equals, Methods::anyInteger),
    NO_INTEGER(null, null),
    STRING(String.class::equals, Methods::anyString),
    NO_STRING(null, Methods::fail),
    BIG_INTEGER(BigInteger.class::equals, Methods::anyBigInteger),
    NO_BIG_INTEGER(null, Methods::fail);

    private final Predicate<Class<?>> predicate;
    private final Function<Class<?>, Object> function;

    private static final Cases<Class<?>, Object> cases =
            Cases.on(AMBIGUOUS).when(INTEGER.predicate).then(INTEGER).orElse(NO_INTEGER)
                 .on(INTEGER).apply(INTEGER.function)
                 .on(NO_INTEGER).when(STRING.predicate).then(STRING).orElse(NO_STRING)
                 .on(STRING).apply(STRING.function)
                 .on(NO_STRING).when(BIG_INTEGER.predicate).then(BIG_INTEGER).orElse(NO_BIG_INTEGER)
                 .on(BIG_INTEGER).apply(BIG_INTEGER.function)
                 .on(NO_BIG_INTEGER).apply(NO_BIG_INTEGER.function)
                 .build();

    Dispenser(final Predicate<Class<?>> predicate, final Function<Class<?>, Object> function) {
        this.predicate = predicate;
        this.function = function;
    }

    static Object any(final Class<?> type) {
        return cases.apply(type);
    }

    private static class Methods {

        private static final Random RANDOM = new Random();

        private static Integer anyInteger(final Class<?> type) {
            return RANDOM.nextInt();
        }

        private static BigInteger anyBigInteger(final Class<?> type) {
            return new BigInteger(64 + RANDOM.nextInt(64), RANDOM);
        }

        private static String anyString(final Class<?> type) {
            return anyBigInteger(type).toString(Character.MAX_RADIX);
        }

        private static Object fail(final Class<?> type) {
            throw new IllegalArgumentException("unsupported type: " + type);
        }
    }
}
