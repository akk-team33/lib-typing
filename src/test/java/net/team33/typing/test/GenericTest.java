package net.team33.typing.test;

import net.team33.typing.Generic;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"AnonymousInnerClass", "AnonymousInnerClassMayBeStatic"})
public class GenericTest {

    private final Generic<String> stringType = new Generic<String>() {
    };

    @Test
    public final void simple() {
        Assert.assertSame(String.class, stringType.getRawClass());
        Assert.assertEquals(0, stringType.getParameters().size());
        Assert.assertEquals(stringType, new Generic<String>() {
        });
    }
}
