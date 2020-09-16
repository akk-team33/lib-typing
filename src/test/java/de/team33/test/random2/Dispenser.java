package de.team33.test.random2;

import de.team33.libs.typing.v4.Type;
import de.team33.libs.typing.v4.RawType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Dispenser {

    private final Stage stage;

    private Dispenser(final Stage stage) {
        this.stage = stage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public final <R> R any(final Class<R> rClass) {
        return any(Type.of(rClass));
    }

    public final <R> R any(final Type<R> rType) {
        //noinspection unchecked
        return (R) any((RawType) rType);
    }

    public final Object any(final RawType type) {
        return getMethod(type).apply(this);
    }

    private Function<Dispenser, ?> getMethod(final RawType type) {
        return stage.methods.computeIfAbsent(type, this::newMethod);
    }

    private Function<Dispenser, ?> newMethod(final RawType type) {
        return DefaultMethods.map(type);
    }

    private enum DefaultMethods {
        UNKNOWN(type -> false, type -> dsp -> {
            throw new IllegalArgumentException("no method specified to get an instance of " + type);
        });

        private final Predicate<RawType> filter;
        private final Function<RawType, Function<Dispenser, ?>> mapping;

        DefaultMethods(final Predicate<RawType> filter, final Function<RawType, Function<Dispenser, ?>> mapping) {
            this.filter = filter;
            this.mapping = mapping;
        }

        public static Function<Dispenser, ?> map(final RawType type) {
            return Stream.of(values())
                         .findAny()
                         .orElse(UNKNOWN).mapping.apply(type);
        }
    }

    private static class Stage implements Supplier<Dispenser> {

        private final Map<RawType, Function<Dispenser, ?>> methods;

        private Stage(final Builder builder) {
            methods = new ConcurrentHashMap<>(builder.methods);
        }

        @Override
        public Dispenser get() {
            return new Dispenser(this);
        }
    }

    public static class Builder {

        public Map<RawType, Function<Dispenser, ?>> methods = new HashMap<>(0);

        private Builder() {
        }

        public <T> Builder putMethod(final Class<T> tClass, final Function<Dispenser, T> method) {
            return putMethod(Type.of(tClass), method);
        }

        public <T> Builder putMethod(final Type<T> tType, final Function<Dispenser, T> method) {
            this.methods.put(tType, method);
            return this;
        }

        public Supplier<Dispenser> prepare() {
            return new Stage(this);
        }
    }
}
