package de.team33.libs.typing.v4;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

abstract class BasicSetup extends TypeSetup {

    private final Consistence consistence;
    private final transient Lazy<Integer> hashView = new Lazy<>(() -> toList().hashCode());

    BasicSetup(final Class<?> primeClass, final List<TypeSetup> actualParameters) {
        this.consistence = new Consistence(primeClass, actualParameters);
    }

    @Override
    public final Class<?> getPrimeClass() {
        return consistence.primeClass;
    }

    @Override
    public final List<TypeSetup> getActualParameters() {
        return consistence.actualParameters;
    }

    @Override
    public final int hashCode() {
        return hashView.get();
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof TypeSetup) && toList().equals(((TypeSetup) obj).toList()));
    }

    @Override
    final List<?> toList() {
        return consistence;
    }

    private static final class Consistence extends AbstractList<Object> {

        private static final List<Function<Consistence, Object>> ELEMENTS = Arrays.asList(
                consistence -> consistence.primeClass,
                consistence -> consistence.actualParameters);

        private final Class<?> primeClass;
        private final List<TypeSetup> actualParameters;

        private Consistence(final Class<?> primeClass, final List<TypeSetup> actualParameters) {
            this.primeClass = primeClass;
            this.actualParameters = actualParameters;
        }

        @Override
        public Object get(final int index) {
            return ELEMENTS.get(index).apply(this);
        }

        @Override
        public int size() {
            return ELEMENTS.size();
        }
    }
}
