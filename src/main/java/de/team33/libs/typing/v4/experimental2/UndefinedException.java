package de.team33.libs.typing.v4.experimental2;

import java.util.Set;

public class UndefinedException extends IllegalStateException {

    public UndefinedException(final Set<Object> undefined) {
        super("Some cases are used but not defined: " + undefined);
    }
}
