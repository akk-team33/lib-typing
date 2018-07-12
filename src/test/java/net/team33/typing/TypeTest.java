package net.team33.typing;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"AnonymousInnerClass", "AnonymousInnerClassMayBeStatic"})
public class TypeTest {

    private static final Type<List<String>> LIST_OF_STRING =
            new Type<List<String>>() {
            };
    private static final Type<String> STRING_TYPE =
            new Type<String>() {
            };
    private static final Type<Map<List<String>, Map<Double, Set<Integer>>>> MAP_OF_LIST_TO_MAP =
            new Type<Map<List<String>, Map<Double, Set<Integer>>>>() {
            };

    @Test(expected = IllegalStateException.class)
    public final void failDirectGeneric() {
        final Direct<String> direct = new Direct<>();
        Assert.fail("expected to Fail but was " + direct);
    }

    @Test(expected = IllegalStateException.class)
    public final void failIndirect() {
        final Type<?> indirect = new Indirect();
        Assert.fail("expected to Fail but was " + indirect);
    }

    @Test
    public final void simple() {
        Assert.assertSame(String.class, STRING_TYPE.getRawClass());
        Assert.assertEquals(0, STRING_TYPE.getParameters().size());
        Assert.assertEquals(STRING_TYPE, new Type<String>() {
        });
    }

    @Test
    public final void list() {
        Assert.assertSame(List.class, LIST_OF_STRING.getRawClass());
        Assert.assertEquals(1, LIST_OF_STRING.getParameters().size());

        final Type<?> stringType = LIST_OF_STRING.getParameters().get(0);
        Assert.assertSame(String.class, stringType.getRawClass());
        Assert.assertEquals(0, stringType.getParameters().size());

        Assert.assertEquals(LIST_OF_STRING, new Type<List<String>>() {
        });
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void rawList() {
        final Type<List> rawList = new Type<List>() {
        };
        Assert.assertSame(List.class, rawList.getRawClass());
        Assert.assertEquals(0, rawList.getParameters().size());
        Assert.assertEquals(rawList, new Type<List>() {
        });
    }

    @Test
    public final void map() {
        Assert.assertSame(Map.class, MAP_OF_LIST_TO_MAP.getRawClass());
        Assert.assertEquals(2, MAP_OF_LIST_TO_MAP.getParameters().size());

        final Type<?> stringListType = MAP_OF_LIST_TO_MAP.getParameters().get(0);
        Assert.assertSame(List.class, stringListType.getRawClass());
        Assert.assertEquals(1, stringListType.getParameters().size());

        final Type<?> stringType = stringListType.getParameters().get(0);
        Assert.assertSame(String.class, stringType.getRawClass());
        Assert.assertEquals(0, stringType.getParameters().size());

        Assert.assertEquals(
                MAP_OF_LIST_TO_MAP,
                new Type<Map<List<String>, Map<Double, Set<Integer>>>>() {
                }
        );
    }

    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    @Test
    public final void testEquals() {
        Assert.assertEquals(
                new StringSet1(),
                new StringSet2()
        );
        Assert.assertNotEquals(
                new StringList1(),
                new StringSet1()
        );
    }

    @Test
    public final void testContainer() throws NoSuchFieldException {
        final Type<Container<String, Set<Integer>, Map<String, Double>>> containerType =
                new Type<Container<String, Set<Integer>, Map<String, Double>>>() {
                };
        final java.lang.reflect.Type mapType = Container.class.getField("mapContent").getGenericType();
        final java.lang.reflect.Type listType = Container.class.getField("listContent").getGenericType();
        //Type.of(mapType, containerType);
    }

    private static class Direct<T> extends Type<T> {
    }

    @SuppressWarnings("EmptyClass")
    private static class Indirect extends Direct<String> {
    }

    @SuppressWarnings("EmptyClass")
    private static class StringList1 extends Type<List<String>> {
    }

    @SuppressWarnings("EmptyClass")
    private static class StringSet1 extends Type<Set<String>> {
    }

    @SuppressWarnings("EmptyClass")
    private static class StringSet2 extends Type<Set<String>> {
    }

    public static class Container<K, V, E> {

        public Map<K, V> mapContent;
        public List<E> listContent;
    }
}