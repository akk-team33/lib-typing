package de.team33.libs.typing.v3;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

class LateBound {

    private final Map<String, Object> backing = new ConcurrentHashMap<>(0);

    final <T> T get(final String key, final Supplier<T> supplier) {
        //noinspection unchecked
        return Optional.ofNullable((T) backing.get(key))
                .orElseGet(() -> put(key, supplier.get()));
    }

    private <T> T put(final String key, final T value) {
        backing.put(key, value);
        return value;
    }
}
