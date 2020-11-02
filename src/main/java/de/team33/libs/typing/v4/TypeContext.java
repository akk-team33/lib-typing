package de.team33.libs.typing.v4;

import java.lang.reflect.Type;

class TypeContext {

    final java.lang.reflect.Type type;
    final Context context;

    TypeContext(final Type type, final Context context) {
        this.type = type;
        this.context = context;
    }
}
