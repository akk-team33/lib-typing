package de.team33.libs.typing.v4.experimental3;

import java.util.Optional;
import java.util.function.Function;

public interface Case<I, R> {

    boolean isMatching(I input);

    Optional<Function<I,R>> getPositive();

    Optional<Function<I,R>> getNegative();
}
