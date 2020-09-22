package de.team33.libs.typing.v4;

import static java.util.Collections.singletonList;

class PlainArrayType extends ArrayType {

    PlainArrayType(final Class<?> rawClass) {
        super(rawClass, singletonList(TypeMapper.map(rawClass.getComponentType())));
    }
}
