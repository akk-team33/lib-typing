package de.team33.test.typing.v3;

import de.team33.libs.typing.v3.Type;
import de.team33.test.typing.shared.Generic;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@SuppressWarnings({"AnonymousInnerClass", "AnonymousInnerClassMayBeStatic"})
public class TypeTest {

    private final Type<Generic<String, List<String>, Map<String, List<String>>>> genericType =
            new Type<Generic<String, List<String>, Map<String, List<String>>>>() {
            };

    @Test
    @Ignore
    public final void getUnderlyingClass() {
        assertSame(Generic.class, genericType.getUnderlyingClass());
    }

    @Test
    @Ignore
    public final void getFormalParameters() {
        final List<String> formalParameters = genericType.getFormalParameters();
        assertEquals("T", formalParameters.get(0));
        assertEquals("U", formalParameters.get(1));
        assertEquals("V", formalParameters.get(2));
    }

    @Test
    @Ignore
    public final void getActualParameters() {
        final List<Type<?>> actualParameters = genericType.getActualParameters();
        assertEquals(3, actualParameters.size());
        assertStringType(actualParameters.get(0));
        assertStringListType(actualParameters.get(1));
        assertMapStringToListOfString(actualParameters.get(2));
    }

    @Test
    @Ignore
    public final void getMemberType() throws NoSuchFieldException {
        assertIntType(genericType.getMemberType(Generic.class.getField("intField").getGenericType()));
        assertIntArrayType(genericType.getMemberType(Generic.class.getField("intArray").getGenericType()));

        assertStringType(genericType.getMemberType(Generic.class.getField("stringField").getGenericType()));
        assertStringArrayType(genericType.getMemberType(Generic.class.getField("stringArray").getGenericType()));

        assertStringType(genericType.getMemberType(Generic.class.getField("tField").getGenericType()));
        assertStringArrayType(genericType.getMemberType(Generic.class.getField("tArray").getGenericType()));

        assertStringListType(genericType.getMemberType(Generic.class.getField("uField").getGenericType()));
        assertStringListArrayType(genericType.getMemberType(Generic.class.getField("uArray").getGenericType()));

        assertMapStringToListOfString(genericType.getMemberType(Generic.class.getField("vField").getGenericType()));
        assertMapStringToListArrayType(genericType.getMemberType(Generic.class.getField("vArray").getGenericType()));

        assertStringListType(genericType.getMemberType(Generic.class.getField("tList").getGenericType()));
        assertMapStringToListOfString(genericType.getMemberType(Generic.class.getField("t2uMap").getGenericType()));
    }

    @Test
    @Ignore
    public final void testEquals() {
        assertEquals(genericType, new Type<Generic<String, List<String>, Map<String, List<String>>>>() {
        });
    }

    @Test
    @Ignore
    public final void testHashcode() {
        assertEquals(
                genericType.hashCode(),
                new Type<Generic<String, List<String>, Map<String, List<String>>>>() {
                }.hashCode()
        );
    }

    private static void assertIntType(final Type<?> intType) {
        assertSame(Integer.TYPE, intType.getUnderlyingClass());
        assertEquals(0, intType.getActualParameters().size());
        assertEquals(intType, Type.of(Integer.TYPE));
    }

    private static void assertIntArrayType(final Type<?> intArrayType) {
        assertArrayType(intArrayType, int[].class, TypeTest::assertIntType);
    }

    private static void assertStringType(final Type<?> stringType) {
        assertSame(String.class, stringType.getUnderlyingClass());
        assertEquals(0, stringType.getActualParameters().size());
        assertEquals(stringType, new Type<String>() {
        });
    }

    private static void assertStringArrayType(final Type<?> stringArrayType) {
        assertArrayType(stringArrayType, String[].class, TypeTest::assertStringType);
    }

    public static void assertStringListType(final Type<?> stringListType) {
        assertSame(List.class, stringListType.getUnderlyingClass());

        final List<Type<?>> parameters = stringListType.getActualParameters();
        assertEquals(1, parameters.size());
        assertStringType(parameters.get(0));

        assertEquals(stringListType, new Type<List<String>>() {
        });
    }

    public static void assertStringListArrayType(final Type<?> stringListArrayType) {
        assertArrayType(stringListArrayType, List[].class, TypeTest::assertStringListType);
    }

    private static void assertMapStringToListOfString(final Type<?> mapType) {
        assertSame(Map.class, mapType.getUnderlyingClass());

        final List<Type<?>> parameters = mapType.getActualParameters();
        assertEquals(2, parameters.size());
        assertStringType(parameters.get(0));
        assertStringListType(parameters.get(1));
    }

    private static void assertMapStringToListArrayType(final Type<?> mapArrayType) {
        assertArrayType(mapArrayType, Map[].class, TypeTest::assertMapStringToListOfString);
    }

    private static void assertArrayType(final Type<?> arrayType,
                                        final Class<?> underlying,
                                        final Consumer<Type<?>> assertComponentType) {
        assertSame(underlying, arrayType.getUnderlyingClass());
        assertEquals(1, arrayType.getActualParameters().size());
        assertComponentType.accept(arrayType.getActualParameters().get(0));
    }
}
