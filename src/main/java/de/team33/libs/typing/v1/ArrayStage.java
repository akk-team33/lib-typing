package de.team33.libs.typing.v1;

import java.util.Collections;
import java.util.List;

abstract class ArrayStage extends Stage {

    @Override
    final List<String> getFormalParameters() {
        return Collections.singletonList("E");
    }
}
