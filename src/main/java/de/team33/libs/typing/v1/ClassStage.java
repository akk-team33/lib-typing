package de.team33.libs.typing.v1;

class ClassStage extends Stage {

    private final Class<?> underlyingClass;
    private final ParameterMap parameters;

    ClassStage(final Class<?> underlyingClass) {
        this.underlyingClass = underlyingClass;
        this.parameters = underlyingClass.isArray()
                ? GenericArrayStage.newArrayParameterMap(DefType.of(underlyingClass.getComponentType()))
                : ParameterMap.EMPTY;
    }

    @Override
    final Class<?> getUnderlyingClass() {
        return underlyingClass;
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    @Override
    final ParameterMap getParameters() {
        return parameters;
    }
}
