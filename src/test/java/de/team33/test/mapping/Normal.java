package de.team33.test.mapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Normal {

    public Optional<Map<String, Normal>> asMap() {
        return Optional.empty();
    }

    public Optional<List<Normal>> asList() {
        return Optional.empty();
    }

    public Optional<String> asElement() {
        return Optional.empty();
    }
}
