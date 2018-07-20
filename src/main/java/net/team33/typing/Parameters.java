package net.team33.typing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

@SuppressWarnings("unused")
public class Parameters {

    public static final Parameters EMPTY = new Parameters();

    private final List<String> formal;
    private final List<DefiniteType<?>> actual;

    Parameters(final List<String> formal, final List<DefiniteType<?>> actual) {
        if (formal.size() == actual.size()) {
            this.formal = unmodifiableList(new ArrayList<>(formal));
            this.actual = unmodifiableList(new ArrayList<>(actual));
        } else {
            throw new IllegalArgumentException(String.format(
                    "formal and actual must match in size but was%n\tformal: %s%n\tactual: %s", formal, actual));
        }
    }

    Parameters() {
        this.formal = emptyList();
        this.actual = emptyList();
    }

    public final List<String> getFormal() {
        // already is immutable ...
        // noinspection AssignmentOrReturnOfFieldWithMutableType
        return formal;
    }

    public final List<DefiniteType<?>> getActual() {
        // already is immutable ...
        // noinspection AssignmentOrReturnOfFieldWithMutableType
        return actual;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(formal, actual);
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof Parameters) && isEqual((Parameters) obj));
    }

    private boolean isEqual(final Parameters other) {
        return formal.equals(other.formal) && actual.equals(other.actual);
    }

    @Override
    public final String toString() {
        return IntStream.range(0, formal.size())
                .mapToObj(index -> formal.get(index) + ": " + actual.get(index))
                .collect(Collectors.joining(", ", "<", ">"));
    }

    public final DefiniteType<?> get(final String name) {
        try {
            return actual.get(formal.indexOf(name));
        } catch (final IndexOutOfBoundsException caught) {
            throw new IllegalArgumentException("not found: " + name, caught);
        }
    }
}
