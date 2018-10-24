package de.team33.libs.typing.v1;

class PlainArrayStage extends ArrayStage {

    private final Class<?> underlyingClass;
    private final ParameterMap parameters;

    PlainArrayStage(final Class<?> underlyingClass) {
        this.underlyingClass = underlyingClass;
        this.parameters = GenericArrayStage.newArrayParameterMap(DefType.of(underlyingClass.getComponentType()));
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
