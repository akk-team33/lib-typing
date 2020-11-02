package de.team33.libs.typing.v4.experimental;

import java.util.Collection;

public class UsedButNotDefinedException extends IllegalStateException {

    UsedButNotDefinedException(final Collection<?> states) {
        super("There are states used but not defined: " + states);
    }
}
