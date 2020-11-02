package de.team33.libs.typing.v4.experimental;

@SuppressWarnings("ProhibitedExceptionDeclared")
@FunctionalInterface
public interface TFunction<I, R, X extends Throwable> {

    R apply(I input) throws Throwable;
}
