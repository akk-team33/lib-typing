package de.team33.libs.typing.v3;

import de.team33.libs.provision.v2.Lazy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class Shape {

    private final transient Lazy<List<Object>> listView =
            new Lazy<>(() -> Arrays.asList(getRawClass(), getActualParameters()));

    private final transient Lazy<Integer> hashView =
            new Lazy<>(() -> listView.get().hashCode());

    /**
     * Returns the raw {@link Class} on which this {@link Shape} is based.
     */
    @SuppressWarnings("rawtypes")
    public abstract Class getRawClass();

    /**
     * Returns the formal type parameters of the (generic) type underlying this {@link Shape}.
     *
     * @see #getActualParameters()
     */
    public abstract List<String> getFormalParameters();

    /**
     * <p>Returns the actual parameters defining this {@link Shape}.</p>
     * <p>The result may be empty even if the {@linkplain #getFormalParameters() formal parameter list} is not.
     * Otherwise the formal and actual parameter list are of the same size and order.</p>
     *
     * @see #getFormalParameters()
     */
    public abstract List<Shape> getActualParameters();

    /**
     * Returns a specific actual parameter based on the corresponding formal parameter.
     */
    public final Shape getActualParameter(final String formalParameter) {
        final List<String> formalParameters = getFormalParameters();
        return Optional.of(formalParameters.indexOf(formalParameter))
                       .filter(index -> 0 <= index)
                       .map(index -> getActualParameters().get(index))
                       .orElseThrow(() -> new IllegalArgumentException(
                               String.format(
                                       "formal parameter <%s> not found in %s",
                                       formalParameter, formalParameters)));
    }

    @Override
    public final int hashCode() {
        return hashView.get();
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof Shape) && listView.get().equals(((Shape) obj).listView.get()));
    }

    @Override
    public abstract String toString();
}
