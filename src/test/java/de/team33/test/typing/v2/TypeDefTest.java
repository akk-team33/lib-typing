package de.team33.test.typing.v2;

import de.team33.libs.typing.v2.TypeDef;
import de.team33.libs.typing.v2.TypeDesc;
import de.team33.test.typing.shared.Generic;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@SuppressWarnings({"AnonymousInnerClass", "AnonymousInnerClassMayBeStatic"})
public class TypeDefTest {

    private final TypeDef<Generic<String, List<String>, Map<String, List<String>>>> genericType =
            new TypeDef<Generic<String, List<String>, Map<String, List<String>>>>() {
            };

    @Test
    public final void getUnderlyingClass() {
        assertSame(Generic.class, genericType.getUnderlyingClass());
    }

    @Test
    public final void getFormalParameters() {
        final List<String> formalParameters = genericType.getFormalParameters();
        assertEquals("T", formalParameters.get(0));
        assertEquals("U", formalParameters.get(1));
        assertEquals("V", formalParameters.get(2));
    }

    @Test
    public final void getActualParameters() {
        final List<TypeDesc> actualParameters = genericType.getActualParameters();
        assertEquals(3, actualParameters.size());
        assertStringType(actualParameters.get(0));
        assertStringListType(actualParameters.get(1));
        assertMapStringToListOfString(actualParameters.get(2));
    }

    @Test
    public final void getMemberType() throws NoSuchFieldException {
        assertIntType(genericType.toTypeDesc(Generic.class.getField("intField").getGenericType()));
        assertIntArrayType(genericType.toTypeDesc(Generic.class.getField("intArray").getGenericType()));

        assertStringType(genericType.toTypeDesc(Generic.class.getField("stringField").getGenericType()));
        assertStringArrayType(genericType.toTypeDesc(Generic.class.getField("stringArray").getGenericType()));

        assertStringType(genericType.toTypeDesc(Generic.class.getField("tField").getGenericType()));
        assertStringArrayType(genericType.toTypeDesc(Generic.class.getField("tArray").getGenericType()));

        assertStringListType(genericType.toTypeDesc(Generic.class.getField("uField").getGenericType()));
        assertStringListArrayType(genericType.toTypeDesc(Generic.class.getField("uArray").getGenericType()));

        assertMapStringToListOfString(genericType.toTypeDesc(Generic.class.getField("vField").getGenericType()));
        assertMapStringToListArrayType(genericType.toTypeDesc(Generic.class.getField("vArray").getGenericType()));

        assertStringListType(genericType.toTypeDesc(Generic.class.getField("tList").getGenericType()));
        assertMapStringToListOfString(genericType.toTypeDesc(Generic.class.getField("t2uMap").getGenericType()));
    }

    @Test
    public final void testIntToString() {
        assertEquals("int", TypeDef.of(Integer.TYPE).toString());
    }

    @Test
    public final void testStringToString() {
        assertEquals("String", TypeDef.of(String.class).toString());
    }

    @Test
    public final void testListToString() {
        assertEquals("List<String>", new TypeDef<List<String>>() {
        }.toString());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testRawListToString() {
        final TypeDef<Generic> listType = new TypeDef<Generic>() {
        };
        assertEquals("Generic", listType.toString());
        assertEquals(0, listType.getParameters().size());
        assertEquals(3, listType.getFormalParameters().size());
        assertEquals(0, listType.getActualParameters().size());
    }

    @Test
    public final void testGenericTypeToString() {
        assertEquals("Generic<String, List<String>, Map<String, List<String>>>", genericType.toString());
    }

    @Test
    public final void testIntArrayToString() {
        assertEquals("int[]", TypeDef.of(int[].class).toString());
    }

    @Test
    public final void testStringArrayToString() {
        assertEquals("String[]", TypeDef.of(String[].class).toString());
    }

    @Test
    public final void testListArrayToString() {
        assertEquals("List<String[]>[][]", new TypeDef<List<String[]>[][]>() {
        }.toString());
    }

    @Test
    public final void testEquals() {
        assertEquals(genericType, new TypeDef<Generic<String, List<String>, Map<String, List<String>>>>() {
        });
    }

    @Test
    public final void testHashcode() {
        assertEquals(
                genericType.hashCode(),
                new TypeDef<Generic<String, List<String>, Map<String, List<String>>>>() {
                }.hashCode()
        );
    }

    private static void assertIntType(final TypeDesc intType) {
        assertSame(Integer.TYPE, intType.getUnderlyingClass());
        assertEquals(0, intType.getActualParameters().size());
        assertEquals(intType, TypeDef.of(Integer.TYPE));
    }

    private static void assertIntArrayType(final TypeDesc intArrayType) {
        assertArrayType(intArrayType, int[].class, TypeDefTest::assertIntType);
    }

    private static void assertStringType(final TypeDesc stringType) {
        assertSame(String.class, stringType.getUnderlyingClass());
        assertEquals(0, stringType.getActualParameters().size());
        assertEquals(stringType, new TypeDef<String>() {
        });
    }

    private static void assertStringArrayType(final TypeDesc stringArrayType) {
        assertArrayType(stringArrayType, String[].class, TypeDefTest::assertStringType);
    }

    public static void assertStringListType(final TypeDesc stringListType) {
        assertSame(List.class, stringListType.getUnderlyingClass());

        final List<TypeDesc> parameters = stringListType.getActualParameters();
        assertEquals(1, parameters.size());
        assertStringType(parameters.get(0));

        assertEquals(stringListType, new TypeDef<List<String>>() {
        });
    }

    public static void assertStringListArrayType(final TypeDesc stringListArrayType) {
        assertArrayType(stringListArrayType, List[].class, TypeDefTest::assertStringListType);
    }

    private static void assertMapStringToListOfString(final TypeDesc mapType) {
        assertSame(Map.class, mapType.getUnderlyingClass());

        final List<TypeDesc> parameters = mapType.getActualParameters();
        assertEquals(2, parameters.size());
        assertStringType(parameters.get(0));
        assertStringListType(parameters.get(1));
    }

    private static void assertMapStringToListArrayType(final TypeDesc mapArrayType) {
        assertArrayType(mapArrayType, Map[].class, TypeDefTest::assertMapStringToListOfString);
    }

    private static void assertArrayType(final TypeDesc arrayType,
                                        final Class<?> underlying,
                                        final Consumer<TypeDesc> assertComponentType) {
        assertSame(underlying, arrayType.getUnderlyingClass());
        assertEquals(1, arrayType.getActualParameters().size());
        assertComponentType.accept(arrayType.getActualParameters().get(0));
    }
}