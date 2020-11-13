package de.team33.test.typing.v4.experimental4;

public class Input {

    public final int a;
    public final int b;
    public final int c;
    public final int d;

    public Input(final int x) {
        this.a = (x >> 3) & 1;
        this.b = (x >> 2) & 1;
        this.c = (x >> 1) & 1;
        this.d = x & 1;
    }

    @Override
    public final String toString() {
        if ((a == 1) && (c == 0)) {
            return "" + a + "_" + c + d;
        } else {
            return "" + a + b + c + d;
        }
    }
}
