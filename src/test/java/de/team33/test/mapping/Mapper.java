package de.team33.test.mapping;

import de.team33.libs.typing.v4.Model;
import de.team33.libs.typing.v4.Type;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Mapper {

    private final Map<Model, Function> mappers = new ConcurrentHashMap<>();

    public final <T> T map(final Object normal, final Class<T> type) {
        return map(normal, Type.of(type));
    }

    public final <T> T map(final Object normal, final Type<T> type) {
        //noinspection unchecked
        return Optional.ofNullable((Function<Object, T>) mappers.get(type))
                       .orElseGet(() -> {
                           final Function<Object, T> result = newMapper(type);
                           mappers.put(type, result);
                           return result;
                       }).apply(normal);
    }

    private <T> Function<Object, T> newMapper(final Type<T> type) {
        return Stream.of(DefaultMapping.values())
                     .filter(value -> value.filter.test(type.getRawClass()))
                     .findAny()
                     .orElse(DefaultMapping.FALLBACK)
                     .mapper(this, type);
    }

    private <T extends Enum<T>> Function newEnumMapper(final Class rawClass) {
        return normal -> Enum.valueOf(rawClass, normal.toString());
    }

    private enum DefaultMapping {

        ENUM(Class::isEnum, (mapper, shape) -> mapper.newEnumMapper(shape.getRawClass())),
        FALLBACK(c -> false, null);

        private final Predicate<Class> filter;
        private final BiFunction<Mapper, Model, Function> biFunction;

        DefaultMapping(final Predicate<Class> filter, final BiFunction<Mapper, Model, Function> biFunction) {
            this.filter = filter;
            this.biFunction = biFunction;
        }

        private <T> Function<Object, T> mapper(final Mapper mapper, final Type<T> type) {
            return biFunction.apply(mapper, type);
        }
    }
}
