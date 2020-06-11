package de.team33.test.random;

import de.team33.libs.typing.v4.Shape;
import de.team33.libs.typing.v4.Type;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Dispenser {

    private final Random random = new Random();
    private final Template template;

    private Dispenser(final Template template) {
        this.template = template;
    }

    public static Builder builder() {
        return new Builder();
    }

    public final <R> R any(final Class<R> rClass) {
        return any(Type.of(rClass));
    }

    public final <R> R any(final Type<R> rType) {
        final Function<Dispenser, R> method = getMethod(rType);
        return method.apply(this);
    }

    private <R> Function<Dispenser, R> getMethod(final Type<R> rType) {
        //noinspection unchecked
        return Optional.ofNullable((Function<Dispenser, R>) template.methods.get(rType)).orElseGet(() -> {
            final Function<Dispenser, R> method = newMethod(rType);
            template.methods.put(rType, method);
            return method;
        });
    }

    private <R> Function<Dispenser, R> newMethod(final Type<R> rType) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public final boolean anyBoolean() {
        return new BigInteger(1, random).intValue() == 0;
    }

    public final byte anyByte() {
        return new BigInteger(Byte.SIZE, random).byteValue();
    }

    public final short anyShort() {
        return new BigInteger(Short.SIZE, random).shortValue();
    }

    public final int anyInt() {
        return new BigInteger(Integer.SIZE, random).intValue();
    }

    public final long anyLong() {
        return new BigInteger(Long.SIZE, random).longValue();
    }

    public final float anyFloat() {
        return anyShort() / new BigInteger(Short.SIZE, random).floatValue();
    }

    public final double anyDouble() {
        return anyInt() / new BigInteger(Integer.SIZE, random).doubleValue();
    }

    public final char anyChar() {
        return anyChar("abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ-0123456789+äöüÄÖÜß".toCharArray());
    }

    public final char anyChar(final char[] array) {
        return (char) anyOf(array);
    }

    private Object anyOf(final Object array) {
        final int index = random.nextInt(Array.getLength(array));
        return Array.get(array, index);
    }

    private static final class Template implements Supplier<Dispenser> {

        @SuppressWarnings("rawtypes")
        private final Map<Shape, Function> methods;

        private Template(final Builder builder) {
            methods = new ConcurrentHashMap<>(builder.methods);
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

        @SuppressWarnings("rawtypes")
        private final Map<Shape, Function> methods = new HashMap<>(0);

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

        public final Supplier<Dispenser> prepare() {
            return new Template(this);
        }

        public final Dispenser build() {
            return prepare().get();
        }
    }
}
