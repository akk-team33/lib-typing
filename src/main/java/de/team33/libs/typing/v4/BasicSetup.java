package de.team33.libs.typing.v4;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

abstract class BasicSetup extends Setup {

    private final Fields fields;

    BasicSetup(final Class<?> primeClass, final List<Setup> actualParameters) {
        this.fields = new Fields(primeClass, actualParameters);
    }

    @Override
    public final Class<?> getPrimeClass() {
        return fields.primeClass;
    }

    @Override
    public final List<Setup> getActualParameters() {
        return fields.actualParameters;
    }

    @Override
    final List<?> toList() {
        return fields;
    }

    private static final class Fields extends AbstractList<Object> {

        private static final List<Function<Fields, Object>> ELEMENTS = Arrays.asList(
                fields -> fields.primeClass,
                fields -> fields.actualParameters);

        private final Class<?> primeClass;
        private final List<Setup> actualParameters;

        private Fields(final Class<?> primeClass, final List<Setup> actualParameters) {
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
