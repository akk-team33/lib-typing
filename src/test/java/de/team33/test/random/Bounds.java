package de.team33.test.random;

class Bounds {

    public final int lower;
    public final int upper;
    public final int distance;

    Bounds(final int lower, final int upper) {
        this.lower = lower;
        this.upper = upper;
        this.distance = upper - lower;
    }
}
