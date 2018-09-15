package de.team33.libs.typing.v2;

final class PlainDesc {

    private PlainDesc() {
    }

    static TypeDesc of(final Class<?> type) {
        return type.isArray() ? new PlainArrayDesc(type) : new ClassDesc(type);
    }
}
