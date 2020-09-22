package de.team33.libs.typing.v4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

abstract class BasicType extends RawType {

    private final Class<?> primeClass;
    private final List<RawType> actualParameters;
    private final transient Lazy<Integer> hashValue;
    private final transient Lazy<List<Object>> listView;

    BasicType(final Class<?> primeClass, final List<RawType> actualParameters) {
        this.primeClass = primeClass;
        this.actualParameters = actualParameters;
        this.listView = new Lazy<>(() -> Collections.unmodifiableList(new ArrayList<>(Arrays.asList(
                this.primeClass,
                this.actualParameters
        ))));
        this.hashValue = new Lazy<>(() -> listView.get().hashCode());
    }

    @Override
    public final Class<?> getPrimeClass() {
        return primeClass;
    }

    @Override
    public final List<RawType> getActualParameters() {
        return actualParameters;
    }

    @Override
    final Comparative comparative() {
        return new MyComparative();
    }

    private class MyComparative implements Comparative {

        @Override
        public final int relativeHashCode() {
            return hashValue.get();
        }

        @Override
        public final boolean relativeEquals(final RawType other) {
            return (other instanceof BasicType)
                    ? relativeEquals((BasicType) other)
                    : other.comparative().relativeEquals(BasicType.this);
        }

        @SuppressWarnings("OverloadedMethodsWithSameNumberOfParameters")
        private boolean relativeEquals(final BasicType other) {
            return listView.get().equals(other.listView.get());
        }
    }
}
