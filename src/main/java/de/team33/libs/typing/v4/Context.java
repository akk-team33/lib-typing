package de.team33.libs.typing.v4;

@FunctionalInterface
interface Context {

    Context NULL = formal -> {
        throw new IllegalArgumentException(String.format("formal parameter <%s> not present", formal));
    };

    RawType getActual(String formal);
}
