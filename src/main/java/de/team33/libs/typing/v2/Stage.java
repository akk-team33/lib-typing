package de.team33.libs.typing.v2;

import java.util.List;

abstract class Stage {

    abstract Class<?> getUnderlyingClass();

    abstract List<String> getFormalParameters();

    abstract List<TypeDesc> getActualParameters();
}
