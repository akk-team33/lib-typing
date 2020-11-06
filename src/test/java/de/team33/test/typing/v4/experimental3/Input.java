package de.team33.test.typing.v4.experimental3;

class Input {

    final int a;
    final int b;
    final int c;
    final int d;

    Input(final int x) {
        this.a = (x >> 3) & 1;
        this.b = (x >> 2) & 1;
        this.c = (x >> 1) & 1;
        this.d = x & 1;
    }

    @Override
    public final String toString() {
        return "" + a + b + c + d;
    }
}
