package de.team33.test.typing.v4;

import de.team33.libs.typing.v4.Shape;
import de.team33.libs.typing.v4.Type;
import de.team33.test.typing.shared.Fixed;
import de.team33.test.typing.shared.Generic;
import de.team33.test.typing.shared.Interface;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class TypeTest {

    private static final Type<List<String>> LIST_OF_STRING_TYPE =
            new Type<List<String>>() {
            };
    private static final Type<Generic<String, List<String>, Map<String, List<String>>>> GENERIC_TYPE =
            new Type<Generic<String, List<String>, Map<String, List<String>>>>() {
            };
    @SuppressWarnings("rawtypes")
    private static final Type<Generic> RAW_GENERIC_TYPE =
            new Type<Generic>() {
            };
    private static final Type<Generic<String, List<String>, Map<String, List<String>>>[]> ARRAY_TYPE =
            new Type<Generic<String, List<String>, Map<String, List<String>>>[]>() {
            };

    @Test
    public final void getRawClass() {
        assertSame(Generic.class, GENERIC_TYPE.getRawClass());
    }

    @Test
    public final void getFormalParameters() {
        final List<String> formalParameters = GENERIC_TYPE.getFormalParameters();
        assertEquals("T", formalParameters.get(0));
        assertEquals("U", formalParameters.get(1));
        assertEquals("V", formalParameters.get(2));
    }

    @Test
    public final void getFormalParametersOfArray() {
        final List<String> formalParameters = ARRAY_TYPE.getFormalParameters();
        assertEquals(1, formalParameters.size());
        assertEquals("E", formalParameters.get(0));
    }

    @Test
    public final void getActualParameters() {
        final List<Shape> actualParameters = GENERIC_TYPE.getActualParameters();
        assertEquals(3, actualParameters.size());
        assertStringType(actualParameters.get(0));
        assertStringListType(actualParameters.get(1));
        assertMapStringToListOfString(actualParameters.get(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void getActualParameter() {
        assertStringType(GENERIC_TYPE.getActualParameter("T"));
        assertStringListType(GENERIC_TYPE.getActualParameter("U"));
        assertMapStringToListOfString(GENERIC_TYPE.getActualParameter("V"));

        // -> IllegalArgumentException ...
        assertMapStringToListOfString(GENERIC_TYPE.getActualParameter("W"));
    }

    @Test
    public final void shapeOf() throws NoSuchFieldException {
        assertIntType(GENERIC_TYPE.shapeOf(Generic.class.getField("intField")));
        assertIntArrayType(GENERIC_TYPE.shapeOf(Generic.class.getField("intArray")));

        assertStringType(GENERIC_TYPE.shapeOf(Generic.class.getField("stringField")));
        assertStringArrayType(GENERIC_TYPE.shapeOf(Generic.class.getField("stringArray")));

        assertStringType(GENERIC_TYPE.shapeOf(Generic.class.getField("tField")));
        assertStringArrayType(GENERIC_TYPE.shapeOf(Generic.class.getField("tArray")));

        assertStringListType(GENERIC_TYPE.shapeOf(Generic.class.getField("uField")));
        assertStringListArrayType(GENERIC_TYPE.shapeOf(Generic.class.getField("uArray")));

        assertMapStringToListOfString(GENERIC_TYPE.shapeOf(Generic.class.getField("vField")));
        assertMapStringToListArrayType(GENERIC_TYPE.shapeOf(Generic.class.getField("vArray")));

        assertStringListType(GENERIC_TYPE.shapeOf(Generic.class.getField("tList")));
        assertMapStringToListOfString(GENERIC_TYPE.shapeOf(Generic.class.getField("t2uMap")));
        
        final Field field = Generic.class.getDeclaredField("tField");
        assertEquals(
                Type.of(String.class),
                Type.of(Fixed.class)
                    .shapeOf(field));
    }

    @Test
    public final void getSuperShape() {
        assertEquals(Optional.of(GENERIC_TYPE), Type.of(Fixed.class).getSuperShape());
    }

    @Test
    public final void getSuperTypeEmpty() {
        assertEquals(Optional.empty(), Type.of(Object.class).getSuperShape());
        assertEquals(Optional.empty(), Type.of(int.class).getSuperShape());
        assertEquals(Optional.empty(), Type.of(void.class).getSuperShape());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shapeOfIllegal() throws NoSuchFieldException {
        final Field field = ArrayList.class.getDeclaredField("elementData");
        fail("Should fail but was " + Type.of(Fixed.class).shapeOf(field));
    }

    @Test
    public final void returnShapeOf() throws NoSuchMethodException {
        final Method method1 = Fixed.class.getMethod("toString");
        assertEquals(
                Type.of(String.class),
                Type.of(Fixed.class).returnShapeOf(method1)
        );
        final Method method2 = Interface.class.getMethod("setStringField", String.class);
        assertEquals(
                Type.of(Fixed.class),
                Type.of(Fixed.class).returnShapeOf(method2)
        );
        final Method method3 = Interface.class.getMethod("getTArray");
        assertEquals(
                Type.of(String[].class),
                Type.of(Fixed.class).returnShapeOf(method3)
        );
    }

    @Test
    public final void parameterShapesOf() throws NoSuchMethodException {
        final Method method = Interface.class.getMethod("setTArray", Object[].class);
        assertEquals(
                singletonList(Type.of(String[].class)),
                Type.of(Fixed.class).parameterShapesOf(method));
    }

    @Test
    public final void exceptionShapesOf() throws NoSuchMethodException {
        final Method method = Interface.class.getMethod("setTArray", Object[].class);
        assertEquals(
                Arrays.asList(Type.of(IOException.class), Type.of(NullPointerException.class)),
                Type.of(Fixed.class).exceptionShapesOf(method));
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

    private static void assertIntType(final Shape intType) {
        assertSame(Integer.TYPE, intType.getRawClass());
        assertEquals(0, intType.getActualParameters().size());
        assertEquals(intType, Type.of(Integer.TYPE));
    }

    private static void assertIntArrayType(final Shape intArrayType) {
        assertArrayType(intArrayType, int[].class, TypeTest::assertIntType);
    }

    private static void assertStringType(final Shape stringType) {
        assertSame(String.class, stringType.getRawClass());
        assertEquals(0, stringType.getActualParameters().size());
        assertEquals(stringType, new Type<String>() {
        });
    }

    private static void assertStringArrayType(final Shape stringArrayType) {
        assertArrayType(stringArrayType, String[].class, TypeTest::assertStringType);
    }

    public static void assertStringListType(final Shape stringListType) {
        assertSame(List.class, stringListType.getRawClass());

        final List<Shape> parameters = stringListType.getActualParameters();
        assertEquals(1, parameters.size());
        assertStringType(parameters.get(0));

        assertEquals(LIST_OF_STRING_TYPE, stringListType);
    }

    public static void assertStringListArrayType(final Shape stringListArrayType) {
        assertArrayType(stringListArrayType, List[].class, TypeTest::assertStringListType);
    }

    private static void assertMapStringToListOfString(final Shape mapType) {
        assertSame(Map.class, mapType.getRawClass());

        final List<Shape> parameters = mapType.getActualParameters();
        assertEquals(2, parameters.size());
        assertStringType(parameters.get(0));
        assertStringListType(parameters.get(1));
    }

    private static void assertMapStringToListArrayType(final Shape mapArrayType) {
        assertArrayType(mapArrayType, Map[].class, TypeTest::assertMapStringToListOfString);
    }

    private static void assertArrayType(final Shape arrayType,
                                        final Class<?> underlying,
                                        final Consumer<Shape> assertComponentType) {
        assertSame(underlying, arrayType.getRawClass());
        assertEquals(1, arrayType.getActualParameters().size());
        assertComponentType.accept(arrayType.getActualParameters().get(0));
    }
}
