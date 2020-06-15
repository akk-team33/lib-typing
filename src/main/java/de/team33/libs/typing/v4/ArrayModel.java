package de.team33.libs.typing.v4;

import de.team33.libs.provision.v2.Lazy;

import java.util.Collections;
import java.util.List;

abstract class ArrayModel extends Model {

    private static final List<String> FORMAL_PARAMETERS = Collections.singletonList("E");

    private final transient Lazy<String> stringView = new Lazy<>(() -> getActualParameters().get(0) + "[]");

    @Override
    public final List<String> getFormalParameters() {
        return FORMAL_PARAMETERS;
    }

    @Override
    public final String toString() {
        return stringView.get();
    }
}
