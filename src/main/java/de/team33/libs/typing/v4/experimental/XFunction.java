package de.team33.libs.typing.v4.experimental;

import java.util.function.Function;

@FunctionalInterface
public interface XFunction<I, R, X extends Exception> extends TFunction<I, R, X> {

    @Override
    R apply(I input) throws X;
}
