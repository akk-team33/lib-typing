package de.team33.test.random;

import de.team33.libs.typing.v4.Shape;
import de.team33.libs.typing.v4.Type;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Dispenser {

    private final Template template;

    public final Basics basics;
    public final Selector selector;

    private Dispenser(final Template template) {
        this.template = template;
        this.basics = template.newBasics.apply(template.defaultCharset);
        this.selector = template.newSelector.apply(basics);
    }

    public static Builder builder() {
        return new Builder();
    }

    public final <R> R any(final Class<R> rClass) {
        return any(Type.of(rClass));
    }

    public final <R> R any(final Type<R> rType) {
        //noinspection unchecked
        return (R) any((Shape) rType);
    }

    public final Object any(final Shape shape) {
        final Function<Dispenser, ?> method = getMethod(shape);
        return method.apply(this);
    }

    private Function<Dispenser, ?> getMethod(final Shape shape) {
        return template.methods.computeIfAbsent(shape, key -> GenericMethod.get(this, key));
    }

    private enum GenericMethod {

        ARRAY(Filter.ARRAY, Method.ARRAY),
        STRING(Filter.STRING, Method.STRING),
        FALLBACK(Filter.FALLBACK, Method.FALLBACK);

        @SuppressWarnings("rawtypes")
        private final BiFunction<Dispenser, Shape, Function<Dispenser, ?>> newMethod;
        private final Predicate<Shape> predicate;

        GenericMethod(final Predicate<Shape> predicate,
                      final BiFunction<Dispenser, Shape, Function<Dispenser, ?>> newMethod) {
            this.predicate = predicate;
            this.newMethod = newMethod;
        }

        private static Function<Dispenser, ?> get(final Dispenser ctx, final Shape shape) {
            return Stream.of(values())
                         .filter(value -> value.predicate.test(shape))
                         .findAny()
                         .orElse(FALLBACK)
                    .newMethod
                    .apply(ctx, shape);
        }

        private interface Filter extends Predicate<Shape> {
            Filter ARRAY = shape -> shape.getRawClass().isArray();
            Filter STRING = shape -> shape.getRawClass().isAssignableFrom(String.class);
            Filter FALLBACK = shape -> false;
        }

        private interface Method extends BiFunction<Dispenser, Shape, Function<Dispenser, ?>> {
            Method ARRAY = (dsp, shape) -> new ArrayMethod(shape, dsp.template.arrayBounds);
            Method STRING = (dsp, shape) -> new StringMethod(dsp.template.stringBounds);
            Method FALLBACK = (dsp, shape) -> dspX -> {
                throw new UnsupportedOperationException("Unsupported: no method specified for type " + shape);
            };
        }
    }

    private static final class Template implements Supplier<Dispenser> {

        @SuppressWarnings("rawtypes")
        private final Map<Shape, Function<Dispenser, ?>> methods;
        private final Function<String, Basics> newBasics;
        private final Function<Basics, Selector> newSelector;
        private final String defaultCharset;
        private final Bounds arrayBounds;
        private final Bounds stringBounds;

        private Template(final Builder builder) {
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

    public static final class Builder {

        private static final Map<Type<?>, List<Type<?>>> PRIME_MAP =
                Stream.of(PRIMITIVES)
                      .collect(HashMap::new, Builder::putPrimitives, Map::putAll);
        private static final String DEFAULT_CHARSET =
                "abcdefghijklmnopqrstuvwxyzäöüß-ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ_0123456789 ,.;:@$!?";

        @SuppressWarnings("rawtypes")
        private final Map<Shape, Function<Dispenser, ?>> methods = new HashMap<>(0);

        private Function<String, Basics> newBasics = DefaultBasics::new;
        private Function<Basics, Selector> newSelector = DefaultSelector::new;
        private String defaultCharset = DEFAULT_CHARSET;
        private Bounds arrayBounds = new Bounds(1, 8);
        private Bounds stringBounds = new Bounds(1, 24);

        private static List<Type<?>> matching(final Type<?> type) {
            return Optional.ofNullable(PRIME_MAP.get(type))
                           .orElseGet(() -> Collections.singletonList(type));
        }

        private static void putPrimitives(final Map<Type<?>, List<Type<?>>> map, final Class<?>[] classes) {
            final List<Type<?>> shapes = Stream.of(classes)
                                               .map(Type::of)
                                               .collect(Collectors.toList());
            shapes.forEach(shape -> map.put(shape, shapes));
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
            return new Template(this);
        }

        public final Dispenser build() {
            return prepare().get();
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
}
