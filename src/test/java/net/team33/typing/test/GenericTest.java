package net.team33.typing.test;

import net.team33.typing.Generic;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"AnonymousInnerClass", "AnonymousInnerClassMayBeStatic"})
public class GenericTest {

    @Test
    public final void simple() {
        assertStringType(new Generic<String>() {
        });
    }

    @Test
    public final void derivedSimple() {
        assertStringType(new StringType());
    }

    @Test
    public final void list() {
        assertStringListType(new Generic<List<String>>() {
        });
    }

    public static void assertStringListType(final Generic<?> stringListType) {
        Assert.assertSame(List.class, stringListType.getRawClass());

        final Map<String, Generic<?>> parameters = stringListType.getParameters();
        Assert.assertEquals(1, parameters.size());
        assertStringType(parameters.get("E"));

        Assert.assertEquals(stringListType, new Generic<List<String>>() {
        });
    }

    private static void assertStringType(final Generic<?> stringType) {
        Assert.assertSame(String.class, stringType.getRawClass());
        Assert.assertEquals(0, stringType.getParameters().size());
        Assert.assertEquals(stringType, new Generic<String>() {
        });
    }

    @SuppressWarnings({"AbstractClassWithOnlyOneDirectInheritor", "AbstractClassWithoutAbstractMethods", "EmptyClass"})
    private static class StringType extends Generic<String> {
        private StringType() {
            super();
        }
    }
}
