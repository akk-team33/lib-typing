package de.team33.test.typing.v1;

import de.team33.typing.v1.DefType;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@SuppressWarnings({"AnonymousInnerClass", "AnonymousInnerClassMayBeStatic"})
public class DefTypeTest {

    @Test
    public final void simple() {
        assertStringType(new DefType<String>() {
        });
        assertStringType(DefType.of(String.class));
    }

    @Test
    public final void derivedSimple() {
        assertStringType(new StringType());
    }

    @Test
    public final void stringList() {
        assertStringListType(new DefType<List<String>>() {
        });
    }

    @Test
    public final void rawList() {
        //noinspection rawtypes
        assertRawList(new DefType<List>() {
        });
        assertRawList(DefType.of(List.class));
    }

    @Test
    public final void mapStringToListOfString() {
        assertMapStringToListOfString(new DefType<Map<String, List<String>>>() {
        });
    }

    @Test
    public final void memberType() throws NoSuchFieldException {
        final DefType<Container<String, String, List<String>>> containerType =
                new DefType<Container<String, String, List<String>>>() {
                };
        final DefType<?> listType = containerType.getMemberType(
                Container.class.getField("listContent").getGenericType());
        final DefType<?> mapType = containerType.getMemberType(
                Container.class.getField("mapContent").getGenericType());
        assertStringListType(listType);
        assertMapStringToListOfString(mapType);
    }

    private static void assertMapStringToListOfString(final DefType<?> mapType) {
        assertSame(Map.class, mapType.getUnderlyingClass());

        final List<DefType<?>> parameters = mapType.getActualParameters();
        assertEquals(2, parameters.size());
        assertStringType(parameters.get(0));
        assertStringListType(parameters.get(1));
    }

    private static void assertRawList(final DefType<?> rawListType) {
        assertSame(List.class, rawListType.getUnderlyingClass());

        final List<DefType<?>> parameters = rawListType.getActualParameters();
        assertEquals(0, parameters.size());
    }

    public static void assertStringListType(final DefType<?> stringListType) {
        assertSame(List.class, stringListType.getUnderlyingClass());

        final List<DefType<?>> parameters = stringListType.getActualParameters();
        assertEquals(1, parameters.size());
        assertStringType(parameters.get(0));

        assertEquals(stringListType, new DefType<List<String>>() {
        });
    }

    private static void assertStringType(final DefType<?> stringType) {
        assertSame(String.class, stringType.getUnderlyingClass());
        assertEquals(0, stringType.getActualParameters().size());
        assertEquals(stringType, new DefType<String>() {
        });
    }

    @SuppressWarnings({"AbstractClassWithOnlyOneDirectInheritor", "AbstractClassWithoutAbstractMethods", "EmptyClass"})
    private static final class StringType extends DefType<String> {
    }

    public static class Container<E, K, V> {

        public List<E> listContent;
        public Map<K, V> mapContent;
    }
}
