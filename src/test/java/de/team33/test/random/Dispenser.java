package de.team33.test.random;

import de.team33.libs.typing.v4.TypeSetup;
import de.team33.libs.typing.v4.Type;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Dispenser {

    private static final Class<?>[][] PRIMITIVES = {
            {boolean.class, Boolean.class},
            {byte.class, Byte.class},
            {short.class, Short.class},
            {int.class, Integer.class},
            {long.class, Long.class},
            {float.class, Float.class},
            {double.class, Double.class},
            {char.class, Character.class}
    };

    public final Basics basics;
    public final Selector selector;

    private final Stage stage;

    private Dispenser(final Stage stage) {
        this.stage = stage;
        this.basics = stage.newBasics.apply(stage.defaultCharset);
        this.selector = stage.newSelector.apply(basics);
    }

    public static Builder builder() {
        return new Builder()
                .put(Boolean.class, dsp -> dsp.basics.anyBoolean())
                .put(byte.class, dsp -> dsp.basics.anyByte())
                .put(Short.class, dsp -> dsp.basics.anyShort())
                .put(int.class, dsp -> dsp.basics.anyInt())
                .put(Long.class, dsp -> dsp.basics.anyLong())
                .put(float.class, dsp -> dsp.basics.anyFloat())
                .put(Double.class, dsp -> dsp.basics.anyDouble())
                .put(char.class, dsp -> dsp.basics.anyChar());
    }

    public final <R> R any(final Class<R> rClass) {
        return any(Type.of(rClass));
    }

    public final <R> R any(final Type<R> rType) {
        //noinspection unchecked
        return (R) any((TypeSetup) rType);
    }

    public final Object any(final TypeSetup setup) {
        final Function<Dispenser, ?> method = getMethod(setup);
        return method.apply(this);
    }

    private Function<Dispenser, ?> getMethod(final TypeSetup setup) {
        return stage.methods.computeIfAbsent(setup, GenericMethod::get);
    }

    private enum GenericMethod {

        ENUM(Filter.ENUM, NewMethod.ENUM),
        ARRAY(Filter.ARRAY, NewMethod.ARRAY),
        STRING(Filter.STRING, NewMethod.STRING),
        STREAM(Filter.STREAM, NewMethod.STREAM),
        FALLBACK(Filter.FALLBACK, NewMethod.FALLBACK);

        private final Function<TypeSetup, Function<Dispenser, ?>> newMethod;
        private final Predicate<TypeSetup> filter;

        GenericMethod(final Predicate<TypeSetup> filter,
                      final Function<TypeSetup, Function<Dispenser, ?>> newMethod) {
            this.filter = filter;
            this.newMethod = newMethod;
        }

        private static Function<Dispenser, ?> get(final TypeSetup setup) {
            return Stream.of(values())
                         .filter(value -> value.filter.test(setup))
                         .findAny()
                         .orElse(FALLBACK)
                    .newMethod
                    .apply(setup);
        }

        private interface Filter extends Predicate<TypeSetup> {
            Filter ENUM = model -> model.getPrimeClass().isEnum();
            Filter ARRAY = model -> model.getPrimeClass().isArray();
            Filter STRING = model -> model.getPrimeClass().isAssignableFrom(String.class);
            Filter STREAM = model -> model.getPrimeClass().equals(Stream.class);
            Filter FALLBACK = model -> false;
        }

        private interface NewMethod extends Function<TypeSetup, Function<Dispenser, ?>> {
            NewMethod ENUM = EnumMethod::new;
            NewMethod ARRAY = model -> new ArrayMethod<>(model, dsp -> dsp.stage.arrayBounds);
            NewMethod STRING = ignored -> new StringMethod(dsp -> dsp.stage.stringBounds);
            NewMethod STREAM = model -> new StreamMethod(model, dsp -> dsp.stage.arrayBounds);
            NewMethod FALLBACK = model -> dspX -> {
                throw new UnsupportedOperationException("Unsupported: no method specified for type " + model);
            };
        }
    }

    public interface Basics {

        boolean anyBoolean();

        byte anyByte();

        short anyShort();

        int anyInt();

        int anyInt(int bound);

        long anyLong();

        float anyFloat();

        double anyDouble();

        char anyChar();

        char anyCharOf(char[] charset);
    }

    public interface Selector {

        boolean anyOf(boolean[] values);

        byte anyOf(byte[] values);

        short anyOf(short[] values);

        int anyOf(int[] values);

        long anyOf(long[] values);

        float anyOf(float[] values);

        double anyOf(double[] values);

        char anyOf(char[] values);

        <T> T anyOf(T[] values);
    }

    private static final class Stage implements Supplier<Dispenser> {

        private final Map<TypeSetup, Function<Dispenser, ?>> methods;
        private final Function<String, Basics> newBasics;
        private final Function<Basics, Selector> newSelector;
        private final String defaultCharset;
        private final Bounds arrayBounds;
        private final Bounds stringBounds;

        private Stage(final Builder builder) {
            methods = new ConcurrentHashMap<>(builder.methods);
            newBasics = builder.newBasics;
            newSelector = builder.newSelector;
            defaultCharset = builder.defaultCharset;
            arrayBounds = builder.arrayBounds;
            stringBounds = builder.stringBounds;
        }

        @Override
        public final Dispenser get() {
            return new Dispenser(this);
        }
    }

    public static final class Builder {

        private static final Map<Type<?>, List<Type<?>>> PRIME_MAP =
                Stream.of(PRIMITIVES)
                      .collect(HashMap::new, Builder::putPrimitives, Map::putAll);
        private static final String DEFAULT_CHARSET =
                "abcdefghijklmnopqrstuvwxyzäöüß-ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ_0123456789 ,.;:@$!?";

        private final Map<TypeSetup, Function<Dispenser, ?>> methods = new HashMap<>(0);

        private Function<String, Basics> newBasics = DefaultBasics::new;
        private Function<Basics, Selector> newSelector = DefaultSelector::new;
        private String defaultCharset = DEFAULT_CHARSET;
        private Bounds arrayBounds = new Bounds(1, 8);
        private Bounds stringBounds = new Bounds(1, 24);

        private Builder() {
        }

        private static List<Type<?>> matching(final Type<?> type) {
            return Optional.ofNullable(PRIME_MAP.get(type))
                           .orElseGet(() -> Collections.singletonList(type));
        }

        private static void putPrimitives(final Map<Type<?>, List<Type<?>>> map, final Class<?>[] classes) {
            final List<Type<?>> models = Stream.of(classes)
                                               .map(Type::of)
                                               .collect(Collectors.toList());
            models.forEach(model -> map.put(model, models));
        }

        public final <T> Builder put(final Class<T> type, final Function<Dispenser, T> method) {
            return put(Type.of(type), method);
        }

        public final <T> Builder put(final Type<T> type, final Function<Dispenser, T> method) {
            matching(type).forEach(match -> methods.put(match, method));
            return this;
        }

        public final Builder setNewBasics(final Function<String, Basics> newBasics) {
            this.newBasics = newBasics;
            return this;
        }

        public final Builder setNewSelector(final Function<Basics, Selector> newSelector) {
            this.newSelector = newSelector;
            return this;
        }

        public final Builder setDefaultCharset(final char[] defaultCharset) {
            return setDefaultCharset(new String(defaultCharset));
        }

        public final Builder setDefaultCharset(final String defaultCharset) {
            this.defaultCharset = defaultCharset;
            return this;
        }

        public final Builder setArrayBounds(final int lower, final int upper) {
            this.arrayBounds = new Bounds(lower, upper);
            return this;
        }

        public final Builder setStringBounds(final int lower, final int upper) {
            this.stringBounds = new Bounds(lower, upper);
            return this;
        }

        public final Supplier<Dispenser> prepare() {
            return new Stage(this);
        }

        public final Dispenser build() {
            return prepare().get();
        }
    }
}
