package de.team33.libs.typing.v1;

import java.util.List;

abstract class Stage {

    abstract Class<?> getUnderlyingClass();

    abstract ParameterMap getParameters();

    List<String> getFormalParameters() {
        return getParameters().getFormal();
    }

    List<DefType<?>> getActualParameters() {
        return getParameters().getActual();
    }
}
