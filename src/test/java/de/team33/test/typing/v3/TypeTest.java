package de.team33.test.typing.v3;

import de.team33.libs.typing.v3.Type;
import de.team33.test.typing.shared.Fixed;
import de.team33.test.typing.shared.Generic;
import de.team33.test.typing.shared.Interface;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

@SuppressWarnings("rawtypes")
public class TypeTest {

    private static final Type<List<String>> LIST_OF_STRING_TYPE =
            new Type<List<String>>() {
            };
    private static final Type<Generic<String, List<String>, Map<String, List<String>>>> GENERIC_TYPE =
            new Type<Generic<String, List<String>, Map<String, List<String>>>>() {
            };
    public static final Type<Generic> RAW_GENERIC_TYPE =
            new Type<Generic>() {
            };

    @Test
    public final void getUnderlyingClass() {
        assertSame(Generic.class, GENERIC_TYPE.getUnderlyingClass());
    }

    @Test
    public final void getFormalParameters() {
        final List<String> formalParameters = GENERIC_TYPE.getFormalParameters();
        assertEquals("T", formalParameters.get(0));
        assertEquals("U", formalParameters.get(1));
        assertEquals("V", formalParameters.get(2));
    }

    @Test
    public final void getActualParameters() {
        final List<Type<?>> actualParameters = GENERIC_TYPE.getActualParameters();
        assertEquals(3, actualParameters.size());
        assertStringType(actualParameters.get(0));
        assertStringListType(actualParameters.get(1));
        assertMapStringToListOfString(actualParameters.get(2));
    }

    @Test
    public final void getMemberType() throws NoSuchFieldException {
        assertIntType(GENERIC_TYPE.getMemberType(Generic.class.getField("intField").getGenericType()));
        assertIntArrayType(GENERIC_TYPE.getMemberType(Generic.class.getField("intArray").getGenericType()));

        assertStringType(GENERIC_TYPE.getMemberType(Generic.class.getField("stringField").getGenericType()));
        assertStringArrayType(GENERIC_TYPE.getMemberType(Generic.class.getField("stringArray").getGenericType()));

        assertStringType(GENERIC_TYPE.getMemberType(Generic.class.getField("tField").getGenericType()));
        assertStringArrayType(GENERIC_TYPE.getMemberType(Generic.class.getField("tArray").getGenericType()));

        assertStringListType(GENERIC_TYPE.getMemberType(Generic.class.getField("uField").getGenericType()));
        assertStringListArrayType(GENERIC_TYPE.getMemberType(Generic.class.getField("uArray").getGenericType()));

        assertMapStringToListOfString(GENERIC_TYPE.getMemberType(Generic.class.getField("vField").getGenericType()));
        assertMapStringToListArrayType(GENERIC_TYPE.getMemberType(Generic.class.getField("vArray").getGenericType()));

        assertStringListType(GENERIC_TYPE.getMemberType(Generic.class.getField("tList").getGenericType()));
        assertMapStringToListOfString(GENERIC_TYPE.getMemberType(Generic.class.getField("t2uMap").getGenericType()));
    }

    @Test
    public final void getSuperType() {
        assertEquals(Optional.of(GENERIC_TYPE), Type.of(Fixed.class).getSuperType());
    }

    @Test
    public final void getSuperTypeEmpty() {
        assertEquals(Optional.empty(), Type.of(Object.class).getSuperType());
        assertEquals(Optional.empty(), Type.of(int.class).getSuperType());
        assertEquals(Optional.empty(), Type.of(void.class).getSuperType());
    }

    @Test
    public final void typeOf() throws NoSuchFieldException {
        final Field field = Generic.class.getDeclaredField("tField");
        assertEquals(
                Type.of(String.class),
                Type.of(Fixed.class).typeOf(field)
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public final void typeOfIllegal() throws NoSuchFieldException {
        final Field field = ArrayList.class.getDeclaredField("elementData");
        fail("Should fail but was " + Type.of(Fixed.class).typeOf(field));
    }

    @Test
    public final void returnTypeOf() throws NoSuchMethodException {
        final Method method1 = Fixed.class.getMethod("toString");
        assertEquals(
                Type.of(String.class),
                Type.of(Fixed.class).returnTypeOf(method1)
        );
        final Method method2 = Interface.class.getMethod("setStringField", String.class);
        assertEquals(
                Type.of(Fixed.class),
                Type.of(Fixed.class).returnTypeOf(method2)
        );
        final Method method3 = Interface.class.getMethod("getTArray");
        assertEquals(
                Type.of(String[].class),
                Type.of(Fixed.class).returnTypeOf(method3)
        );
    }

    @Test
    public final void parameterTypesOf() throws NoSuchMethodException {
        final Method method = Interface.class.getMethod("setTArray", Object[].class);
        assertEquals(
                singletonList(Type.of(String[].class)),
                Type.of(Fixed.class).parameterTypesOf(method)
        );
    }

    @Test
    public final void testIntToString() {
        assertEquals("int", Type.of(Integer.TYPE).toString());
    }

    @Test
    public final void testStringToString() {
        assertEquals("String", Type.of(String.class).toString());
    }

    @Test
    public final void testListToString() {
        assertEquals("List<String>", LIST_OF_STRING_TYPE.toString());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testRawGeneric() {
        assertEquals("Generic", RAW_GENERIC_TYPE.toString());
        assertEquals(3, RAW_GENERIC_TYPE.getFormalParameters().size());
        assertEquals(0, RAW_GENERIC_TYPE.getActualParameters().size());
    }

    @Test
    public final void testGenericTypeToString() {
        assertEquals("Generic<String, List<String>, Map<String, List<String>>>", GENERIC_TYPE.toString());
    }

    @Test
    public final void testIntArrayToString() {
        assertEquals("int[]", Type.of(int[].class).toString());
    }

    @Test
    public final void testStringArrayToString() {
        assertEquals("String[]", Type.of(String[].class).toString());
    }

    @Test
    public final void testListArrayToString() {
        assertEquals("List<String[]>[][]", new Type<List<String[]>[][]>() {
        }.toString());
    }

    @Test
    public final void testEquals() {
        assertEquals(GENERIC_TYPE, new Type<Generic<String, List<String>, Map<String, List<String>>>>() {
        });
        assertNotEquals(GENERIC_TYPE, RAW_GENERIC_TYPE);
    }

    @Test
    public final void testHashcode() {
        assertEquals(
                GENERIC_TYPE.hashCode(),
                new Type<Generic<String, List<String>, Map<String, List<String>>>>() {
                }.hashCode()
        );
        assertNotEquals(
                GENERIC_TYPE.hashCode(),
                RAW_GENERIC_TYPE.hashCode()
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

        assertEquals(LIST_OF_STRING_TYPE, stringListType);
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
