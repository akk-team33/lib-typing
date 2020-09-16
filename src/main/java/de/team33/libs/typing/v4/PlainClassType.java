package de.team33.libs.typing.v4;

import static java.util.Collections.emptyList;

class PlainClassType extends DiscreteType {

    PlainClassType(final Class<?> rawClass) {
        super(rawClass, emptyList());
    }
}
