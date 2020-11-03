package de.team33.libs.typing.v4.experimental;

import java.util.function.Function;

@FunctionalInterface
public interface XFunction<I, R, X extends Exception> {

    R apply(I input) throws X;
}
