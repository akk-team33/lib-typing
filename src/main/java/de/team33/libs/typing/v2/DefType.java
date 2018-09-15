package de.team33.libs.typing.v2;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Represents a fully defined type, possibly based on a generic class.
 * </p><p>
 * For example, an instance of {@code DefType<Map<String, List<String>>>}
 * represents the type {@code Map<String, List<String>>}.
 * </p><p>
 * To get an instance of (any) DefType, you first need to create a fully defined derivative of DefType.
 * Only this can be instantiated.
 * The easiest way to achieve this is to use an anonymous derivation with simultaneous instantiation. Example:
 * </p><pre>
 * final DefType&lt;Map&lt;String, List&lt;String&gt;&gt;&gt; mapStringToStringListType
 *         = new DefType&lt;Map&lt;String, List&lt;String&gt;&gt;&gt;() { };
 * </pre><p>
 * If a simple class object already fully defines the type in question,
 * there is a convenience method to obtain an instance of DefType. Example:
 * </p><pre>
 * final DefType&lt;String&gt; stringType
 *         = DefType.of(String.class);
 * </pre><p>
 * <b>Note</b>: This class is defined as an abstract class, but does not define an abstract method
 * to enforce that a derivative is required for an instantiation.
 * </p>
 */
@SuppressWarnings({"AbstractClassWithoutAbstractMethods", "unused"})
public abstract class DefType<T> extends TypeDesc {

    private final TypeDesc backing;

    /**
     * Initializes a {@link DefType} based on its own full definition
     */
    protected DefType() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.backing = TypeType.toDesc(genericSuperclass.getActualTypeArguments()[0], Collections.emptyMap());
    }

    private DefType(final Class<T> simpleClass) {
        this.backing = PlainDesc.of(simpleClass);
    }

    /**
     * Returns a {@link DefType} based on a simple, fully defined {@link Class}.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static <T> DefType<T> of(final Class<T> simpleClass) {
        return new DefType<T>(simpleClass) {
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
}
