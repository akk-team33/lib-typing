package de.team33.libs.typing.v1;

import java.util.List;

abstract class Stage {

    abstract Class<?> getUnderlyingClass();

    abstract ParameterMap getParameters();

    abstract List<String> getFormalParameters();

    abstract List<DefType<?>> getActualParameters();
}
