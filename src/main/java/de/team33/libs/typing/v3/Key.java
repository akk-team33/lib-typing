package de.team33.libs.typing.v3;

import java.util.Arrays;

/**
 * Abstracts keys with an identity semantic and reference to a specific type.
 */
public class Key<T> {

    private final String creation;

    /**
     * Initializes an instance so that the {@link #toString()} method lets you infer where in the source code the
     * instance was created.
     */
    public Key() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        creation = (2 < stackTrace.length)
                ? stackTrace[2].toString()
                : ("unknown(" + Arrays.toString(stackTrace) + ")");
    }

    /**
     * Returns a string representation that allows conclusions about where in source code that instance was created.
     */
    @Override
    public final String toString() {
        return creation;
    }
}
