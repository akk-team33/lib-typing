package de.team33.libs.typing.v4.experimental;

import java.util.function.Function;

@FunctionalInterface
public interface RFunction<I, R> extends Function<I, R>, TFunction<I, R, RuntimeException> {

    @Override
    R apply(I input);
}
