package net.team33.typing.test;

import net.team33.typing.Generic;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

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

        final Map<String, Generic<?>> parameters = mapType.getParameters();
        assertEquals(2, parameters.size());
        assertStringType(parameters.get("K"));
        assertStringListType(parameters.get("V"));
    }

    private static void assertRawList(final Generic<?> rawListType) {
        assertSame(List.class, rawListType.getRawClass());

        final Map<String, Generic<?>> parameters = rawListType.getParameters();
        assertEquals(0, parameters.size());
        assertNull(parameters.get("E"));
    }

    public static void assertStringListType(final Generic<?> stringListType) {
        assertSame(List.class, stringListType.getRawClass());

        final Map<String, Generic<?>> parameters = stringListType.getParameters();
        assertEquals(1, parameters.size());
        assertStringType(parameters.get("E"));

        assertEquals(stringListType, new Generic<List<String>>() {
        });
    }

    private static void assertStringType(final Generic<?> stringType) {
        assertSame(String.class, stringType.getRawClass());
        assertEquals(0, stringType.getParameters().size());
        assertEquals(stringType, new Generic<String>() {
        });
    }

    @SuppressWarnings({"AbstractClassWithOnlyOneDirectInheritor", "AbstractClassWithoutAbstractMethods", "EmptyClass"})
    private static class StringType extends Generic<String> {
        private StringType() {
            super();
        }
    }

    public static class Container<E, K, V> {

        public List<E> listContent;
        public Map<K, V> mapContent;
    }
}
