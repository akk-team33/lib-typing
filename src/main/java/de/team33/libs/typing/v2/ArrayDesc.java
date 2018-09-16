package de.team33.libs.typing.v2;

import java.util.Collections;
import java.util.List;

abstract class ArrayDesc extends TypeDesc {

    @Override
    public final List<String> getFormalParameters() {
        return Collections.singletonList("E");
    }

    @Override
    public final String toString() {
        return getActualParameters().get(0) + "[]";
    }
}
