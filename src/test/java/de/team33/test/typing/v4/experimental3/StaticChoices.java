package de.team33.test.typing.v4.experimental3;

class StaticChoices {

    static String map(final Input input) {
        if (0 == input.a) {
            if (0 == input.b) {
                if (0 == input.c) {
                    if (0 == input.d) {
                        return "0000";
                    } else {
                        return "0001";
                    }
                } else {
                    if (0 == input.d) {
                        return "0010";
                    } else {
                        return "0011";
                    }
                }
            } else {
                if (0 == input.c) {
                    if (0 == input.d) {
                        return "0100";
                    } else {
                        return "0101";
                    }
                } else {
                    if (0 == input.d) {
                        return "0110";
                    } else {
                        return "0111";
                    }
                }
            }
        } else {
            if (0 == input.b) {
                if (0 == input.c) {
                    if (0 == input.d) {
                        return "1000";
                    } else {
                        return "1001";
                    }
                } else {
                    if (0 == input.d) {
                        return "1010";
                    } else {
                        return "1011";
                    }
                }
            } else {
                if (0 == input.c) {
                    if (0 == input.d) {
                        return "1100";
                    } else {
                        return "1101";
                    }
                } else {
                    if (0 == input.d) {
                        return "1110";
                    } else {
                        return "1111";
                    }
                }
            }
        }
    }
}
