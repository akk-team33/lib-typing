package de.team33.libs.typing.v4;

import java.lang.reflect.ParameterizedType;
import java.util.List;


/**
 * <p>Represents a definite type that can be based on a generic as well as a non-generic class.</p>
 * <p>Just as an instance of <b>{@code Class<String>}</b> represents the <b>class {@code String}</b>, an instance of
 * <b>{@code Type<String>}</b> represents the <b>type {@code String}</b>, with no semantic difference between the
 * <b>class</b> {@code String} and the <b>type</b> {@code String}.</p>
 * <p>In addition, an instance of <b>{@code Type<Map<String, List<String>>>}</b> represents the (parameterized) type
 * <b>{@code Map<String, List<String>>}</b>, while a corresponding instance of
 * <b>{@code Class<Map<String, List<String>>>}</b> is not possible.</p>
 * <p>To get an instance of Type, you need to create a definite derivative of Type. Example:</p>
 * <pre>
 * // Generic Type ...
 *
 * public class MapStringToStringListType extends Type&lt;Map&lt;String, List&lt;String&gt;&gt;&gt;() {
 * }
 *
 * final Type&lt;Map&lt;String, List&lt;String&gt;&gt;&gt; mapStringToStringListType =
 *         new MapStringToStringListType();
 * </pre><pre>
 * // Simple Type ...
 *
 * public class StringType extends Type&lt;String&gt;() {
 * }
 *
 * final Type&lt;String&gt; stringType =
 *         new StringType();
 * </pre>
 * <p>A more convenient way to achieve this is to use an anonymous derivation with simultaneous instantiation.
 * Examples:</p>
 * <pre>
 * // Generic Type ...
 *
 * final Type&lt;Map&lt;String, List&lt;String&gt;&gt;&gt; mapStringToStringListType =
 *         new Type&lt;Map&lt;String, List&lt;String&gt;&gt;&gt;() { };
 * </pre><pre>
 * // Simple Type ...
 *
 * final Type&lt;String&gt; stringType =
 *         new Type&lt;String&gt;() { };
 * </pre><p>
 * If, as in the last case, a simple class already fully defines the type concerned, there is an even more convenient
 * method to get a corresponding Type instance. Example:
 * </p><pre>
 * // Simple Type ...
 *
 * final Type&lt;String&gt; stringType =
 *         Type.of(String.class);
 * </pre>
 *
 * @see #Type()
 * @see #of(Class)
 */
public abstract class Type<T> extends Shape {

    public final Shape backing;

    /**
     * Initializes a {@link Type} based on its well-defined derivative.
     */
    protected Type() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.backing = TypeMapper.map(
                genericSuperclass.getActualTypeArguments()[0],
                ClassMapper.map(getClass()));
    }

    private Type(final Shape backing) {
        this.backing = backing;
    }

    /**
     * <p>Returns a {@link Type} based on a simple {@link Class}.</p>
     * <p>Such a {@link Type} has no {@linkplain #getActualParameters() actual parameters}, but may have
     * {@linkplain #getFormalParameters() formal parameters}.</p>
     */
    public static <T> Type<T> of(final Class<T> simpleClass) {
        return new Type<T>(ClassMapper.map(simpleClass)) {
        };
    }

    @Override
    public final Class<?> getRawClass() {
        return backing.getRawClass();
    }

    @Override
    public final List<String> getFormalParameters() {
        return backing.getFormalParameters();
    }

    @Override
    public final List<Shape> getActualParameters() {
        return backing.getActualParameters();
    }

    @Override
    public final String toString() {
        return backing.toString();
    }
}
