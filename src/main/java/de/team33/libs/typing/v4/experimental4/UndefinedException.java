package de.team33.libs.typing.v4.experimental4;

import java.util.Set;

public class UndefinedException extends IllegalStateException {

    private static final String MESSAGE = "%n%n" +
            "Some cases (at least one) are referenced but not defined: %s%n" +
            "This may be fixed (for each case mentioned) by ...%n%n" +
            "a) ... defining a case that uses the case as a <precondition> or ...%n" +
            "b) ... not specifying a <condition> for the opposite of the case or ...%n" +
            "c) ... setting an end result for the case.";

    public UndefinedException(final Set<Object> undefined) {
        super(String.format(MESSAGE, undefined));
    }
}
