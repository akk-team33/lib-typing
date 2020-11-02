package de.team33.libs.typing.v4.experimental;

import java.util.Collection;

public class DefinedButNotUsedException extends IllegalStateException {

    DefinedButNotUsedException(final Collection<?> states) {
        super("There are states defined but not used: " + states);
    }
}
