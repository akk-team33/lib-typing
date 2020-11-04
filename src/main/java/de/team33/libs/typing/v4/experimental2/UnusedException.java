package de.team33.libs.typing.v4.experimental2;

import java.util.Set;

public class UnusedException extends IllegalStateException {

    UnusedException(final Set<Object> unused) {
        super("Some cases are defined but not used: " + unused);
    }
}
