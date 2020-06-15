package de.team33.libs.typing.v4;

import de.team33.libs.provision.v2.Lazy;

import java.util.List;

import static java.util.Collections.singletonList;

class PlainArrayModel extends ArrayModel {

    private final Class<?> rawClass;

    private final transient Lazy<List<Model>> actualParameters =
            new Lazy<>(() -> singletonList(ClassMapper.map(getRawClass().getComponentType())));

    PlainArrayModel(final Class<?> rawClass) {
        this.rawClass = rawClass;
    }

    @Override
    public final Class<?> getRawClass() {
        return rawClass;
    }

    @Override
    public final List<Model> getActualParameters() {
        return actualParameters.get();
    }
}
