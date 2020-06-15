package de.team33.libs.typing.v4;

import de.team33.libs.provision.v2.Lazy;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.List;

import static java.util.Collections.singletonList;

class GenericArrayModel extends ArrayModel {

    private final Model componentModel;
    private final transient Lazy<Class<?>> rawClass = new Lazy<>(this::newRawClass);

    GenericArrayModel(final GenericArrayType type, final Model context) {
        this.componentModel = TypeMapper.map(type.getGenericComponentType(), context);
    }

    private Class<?> newRawClass() {
        return Array.newInstance(componentModel.getRawClass(), 0).getClass();
    }

    @Override
    public final Class<?> getRawClass() {
        return rawClass.get();
    }

    @Override
    public List<Model> getActualParameters() {
        return singletonList(componentModel);
    }
}
