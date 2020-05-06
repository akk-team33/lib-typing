package de.team33.libs.typing.v3;

import de.team33.libs.provision.v2.Lazy;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.List;

import static java.util.Collections.singletonList;

@SuppressWarnings("rawtypes")
class GenericArrayShape extends ArrayShape {

    private final Shape componentShape;
    private final transient Lazy<Class<?>> rawClass = new Lazy<>(this::newRawClass);

    GenericArrayShape(final GenericArrayType type, final Shape context) {
        this.componentShape = TypeMapper.map(type.getGenericComponentType(), context);
    }

    private Class<?> newRawClass() {
        return Array.newInstance(componentShape.getRawClass(), 0).getClass();
    }

    @Override
    public final Class<?> getRawClass() {
        return rawClass.get();
    }

    @Override
    public List<Shape> getActualParameters() {
        return singletonList(componentShape);
    }
}
