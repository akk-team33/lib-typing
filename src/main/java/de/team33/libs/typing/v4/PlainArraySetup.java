package de.team33.libs.typing.v4;

import static java.util.Collections.singletonList;

class PlainArraySetup extends ArraySetup {

    PlainArraySetup(final Class<?> rawClass) {
        super(rawClass, singletonList(ClassMapper.map(rawClass.getComponentType())));
    }
}
