package de.team33.test.typing.v4.experimental3;

import de.team33.libs.typing.v4.experimental3.Case;
import de.team33.libs.typing.v4.experimental3.Cases;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.team33.libs.typing.v4.experimental3.Case.not;

enum Dispenser implements Case<Class<?>, Function<Random, ?>> {

    INTEGER(null, Filter.INTEGER, Method.INTEGER, null),
    STRING(not(INTEGER), Filter.STRING, Method.STRING, Method.FAIL);

    private static final Cases<Class<?>, Function<Random, ?>> CASES = Cases.build(values());

    private final Case<Class<?>, Function<Random, ?>> preCondition;
    private final Predicate<Class<?>> predicate;
    private final Function<Class<?>, Function<Random, ?>> positive;
    private final Function<Class<?>, Function<Random, ?>> negative;

    Dispenser(final Case<Class<?>, Function<Random, ?>> preCondition,
              final Predicate<Class<?>> predicate,
              final Function<Random, ?> positive,
              final Function<Random, ?> negative) {
        this.preCondition = preCondition;
        this.predicate = predicate;
        this.positive = type -> positive;
        this.negative = (null == negative) ? null : ((Method.FAIL == negative) ? type -> random -> {
            throw new IllegalArgumentException("undefied: " + type);
        } : type -> negative);
    }

    public static <T> T apply(final Class<?> type, final Random random) {
        //noinspection unchecked
        return (T) CASES.apply(type).apply(random);
    }

    @Override
    public Optional<Case<Class<?>, Function<Random, ?>>> getPreCondition() {
        return Optional.ofNullable(preCondition);
    }

    @Override
    public boolean isMatching(final Class<?> input) {
        return predicate.test(input);
    }

    @Override
    public Optional<Function<Class<?>, Function<Random, ?>>> getPositive() {
        return Optional.ofNullable(positive);
    }

    @Override
    public Optional<Function<Class<?>, Function<Random, ?>>> getNegative() {
        return Optional.ofNullable(negative);
    }

    @SuppressWarnings("InnerClassFieldHidesOuterClassField")
    @FunctionalInterface
    private static interface Filter extends Predicate<Class<?>> {
        Filter INTEGER = Integer.class::equals;
        Filter STRING = String.class::equals;
        Filter TRUE = type -> true;
    }

    @SuppressWarnings("InnerClassFieldHidesOuterClassField")
    @FunctionalInterface
    private interface Method<R> extends Function<Random, R> {
        Method<Integer> INTEGER = Random::nextInt;
        Method<BigInteger> BIG_INTEGER = random -> new BigInteger(64 + random.nextInt(64), random);
        Method<String> STRING = random -> BIG_INTEGER.apply(random).toString(Character.MAX_RADIX);
        Method<Void> FAIL = random -> {
            return null;
        };
    }
}
