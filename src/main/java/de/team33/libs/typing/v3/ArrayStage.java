package de.team33.libs.typing.v3;

import java.util.Collections;
import java.util.List;

class ArrayStage extends Stage {

    @Override
    final List<String> getFormalParameters() {
        return Collections.singletonList("E");
    }
}
