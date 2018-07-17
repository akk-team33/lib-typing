package net.team33.typing.test;

import net.team33.typing.Generic;
import net.team33.typing.Parameters;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SuppressWarnings({"AnonymousInnerClass", "AnonymousInnerClassMayBeStatic"})
public class GenericTest {

    @Test
    public final void simple() {
        assertStringType(new Generic<String>() {
        });
        assertStringType(Generic.of(String.class));
    }

    @Test
    public final void derivedSimple() {
        assertStringType(new StringType());
    }

    @Test
    public final void stringList() {
        assertStringListType(new Generic<List<String>>() {
        });
    }

    @Test
    public final void rawList() {
        //noinspection rawtypes
        assertRawList(new Generic<List>() {
        });
        assertRawList(Generic.of(List.class));
    }

    @Test
    public final void mapStringToListOfString() {
        assertMapStringToListOfString(new Generic<Map<String, List<String>>>() {
        });
    }

    @Test
    public final void memberType() throws NoSuchFieldException {
        final Generic<Container<String, String, List<String>>> containerType =
                new Generic<Container<String, String, List<String>>>() {
                };
        final Generic<?> listType = containerType.getMemberType(
                Container.class.getField("listContent").getGenericType());
        final Generic<?> mapType = containerType.getMemberType(
                Container.class.getField("mapContent").getGenericType());
        assertStringListType(listType);
        assertMapStringToListOfString(mapType);
    }

    private static void assertMapStringToListOfString(final Generic<?> mapType) {
        assertSame(Map.class, mapType.getRawClass());

        final Parameters parameters = mapType.getParameters();
        assertEquals(2, parameters.getActual().size());
        assertStringType(parameters.get("K"));
        assertStringListType(parameters.get("V"));
    }

    private static void assertRawList(final Generic<?> rawListType) {
        assertSame(List.class, rawListType.getRawClass());

        final Parameters parameters = rawListType.getParameters();
        assertEquals(0, parameters.getActual().size());
        assertException(() -> parameters.get("E"), IllegalArgumentException.class);
    }

    private static void assertException(final Runnable runnable, final Class<? extends Throwable> exceptionClass) {
        try {
            runnable.run();
            fail("expected: " + exceptionClass.getCanonicalName());
        } catch (final Throwable caught) {
            assertTrue(
                    String.format(
                            "expected %s but was %s",
                            exceptionClass.getCanonicalName(),
                            caught.getClass().getCanonicalName()),
                    exceptionClass.isInstance(caught));
        }
    }

    public static void assertStringListType(final Generic<?> stringListType) {
        assertSame(List.class, stringListType.getRawClass());

        final Parameters parameters = stringListType.getParameters();
        assertEquals(1, parameters.getActual().size());
        assertStringType(parameters.get("E"));

        assertEquals(stringListType, new Generic<List<String>>() {
        });
    }

    private static void assertStringType(final Generic<?> stringType) {
        assertSame(String.class, stringType.getRawClass());
        assertEquals(0, stringType.getParameters().getActual().size());
        assertEquals(stringType, new Generic<String>() {
        });
    }

    @SuppressWarnings({"AbstractClassWithOnlyOneDirectInheritor", "AbstractClassWithoutAbstractMethods", "EmptyClass"})
    private static final class StringType extends Generic<String> {
    }

    public static class Container<E, K, V> {

        public List<E> listContent;
        public Map<K, V> mapContent;
    }
}
