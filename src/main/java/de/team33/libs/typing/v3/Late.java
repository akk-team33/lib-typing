package de.team33.libs.typing.v3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Late {

    @SuppressWarnings("rawtypes")
    private final Map<Key, Supplier> suppliers = new HashMap<>(0);
    @SuppressWarnings("rawtypes")
    private final Map<Key, Object> values = new ConcurrentHashMap<>(0);

    public final <T> Late set(final Key<T> key, final Supplier<T> supplier) {
        suppliers.put(key, supplier);
        return this;
    }

    public final <T> T get(final Key<T> key) {
        //noinspection unchecked
        return Optional.ofNullable((T) values.get(key)).orElseGet(() -> {
            final T result = getSupplier(key).get();
            values.put(key, result);
            return result;
        });
    }

    private <T> Supplier<T> getSupplier(final Key<T> key) {
        //noinspection unchecked
        return Optional.ofNullable((Supplier<T>) suppliers.get(key))
                .orElseThrow(() -> new IllegalArgumentException("unspecified key: " + key));
    }

    public static class Key<T> {
    }
}
