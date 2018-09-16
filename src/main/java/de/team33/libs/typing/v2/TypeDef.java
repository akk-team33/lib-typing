package de.team33.libs.typing.v2;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Represents a type definition: a fully defined {@linkplain TypeDesc type description},
 * possibly based on a generic class.
 * </p><p>
 * For example, an instance of {@code TypeDef<Map<String, List<String>>>}
 * represents the type {@code Map<String, List<String>>}.
 * </p><p>
 * To get an instance of (any) TypeDef, you first need to create a fully defined derivative of TypeDef.
 * Only this can be instantiated.
 * The easiest way to achieve this is to use an anonymous derivation with simultaneous instantiation. Example:
 * </p><pre>
 * final TypeDef&lt;Map&lt;String, List&lt;String&gt;&gt;&gt; mapStringToStringListType
 *         = new TypeDef&lt;Map&lt;String, List&lt;String&gt;&gt;&gt;() { };
 * </pre><p>
 * If a simple class object already fully defines the type in question, there is a convenience method to obtain an
 * instance of TypeDef. Example:
 * </p><pre>
 * final TypeDef&lt;String&gt; stringType
 *         = TypeDef.of(String.class);
 * </pre><p>
 * <b>Note</b>: This class is defined as an abstract class (without abstract methods) to force a derivative for an
 * instantiation.
 * </p>
 */
@SuppressWarnings({"AbstractClassWithoutAbstractMethods", "unused"})
public abstract class TypeDef<T> extends TypeDesc {

    private final TypeDesc backing;

    /**
     * Initializes a derivative of a type definition based on its own full definition
     */
    protected TypeDef() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.backing = TypeType.toDesc(genericSuperclass.getActualTypeArguments()[0], Collections.emptyMap());
    }

    private TypeDef(final Class<T> simpleClass) {
        this.backing = ClassType.toDesc(simpleClass);
    }

    /**
     * A simple type definition based on a simple, fully defined {@link Class}.
     *
     * @param simpleClass the intended underlying {@link Class}.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static <T> TypeDef<T> of(final Class<T> simpleClass) {
        return new TypeDef<T>(simpleClass) {
        };
    }

    @Override
    public final Class<?> getUnderlyingClass() {
        return backing.getUnderlyingClass();
    }

    @Override
    public final List<String> getFormalParameters() {
        return backing.getFormalParameters();
    }

    @Override
    public final List<TypeDesc> getActualParameters() {
        return backing.getActualParameters();
    }

    @Override
    public final String toString() {
        return backing.toString();
    }
}
