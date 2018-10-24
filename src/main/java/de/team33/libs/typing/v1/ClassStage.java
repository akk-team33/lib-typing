package de.team33.libs.typing.v1;

class ClassStage extends SingleStage {

    private final Class<?> underlyingClass;

    ClassStage(final Class<?> underlyingClass) {
        this.underlyingClass = underlyingClass;
    }

    @Override
    final Class<?> getUnderlyingClass() {
        return underlyingClass;
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    @Override
    final ParameterMap getParameters() {
        return ParameterMap.EMPTY;
    }
}
