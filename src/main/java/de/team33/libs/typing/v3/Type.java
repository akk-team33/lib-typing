package de.team33.libs.typing.v3;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * <p>
 * Represents a definite type description that can be based on a generic as well as a non-generic class. Examples:
 * </p><ul>
 * <li>an instance of <strong>{@code Type<Map<String, List<String>>>}</strong> represents the type
 * <strong>{@code Map<String, List<String>>}</strong>.</li>
 * <li>an instance of <strong>{@code Type<String>}</strong> represents the type <strong>{@code String}</strong>.</li>
 * </ul><p>
 * To get an instance of Type, you need to create a definite derivative of Type.
 * The easiest way to achieve this is to use an anonymous derivation with simultaneous instantiation. Examples:
 * </p><pre>
 * final Type&lt;Map&lt;String, List&lt;String&gt;&gt;&gt; mapStringToListOfStringType
 *         = new Type&lt;Map&lt;String, List&lt;String&gt;&gt;&gt;() { };
 * </pre><pre>
 * final Type&lt;String&gt; stringType
 *         = new Type&lt;String&gt;() { };
 * </pre><p>
 * If, as in the last case, a simple class already fully defines the type concerned, there is a convenience method to
 * get a corresponding Type instance. Example:
 * </p><pre>
 * final Type&lt;String&gt; stringType
 *         = Type.of(String.class);
 * </pre><p>
 * <b>Note</b>: This class is defined as an abstract class without defining an abstract method to enforce that a
 * derivative is required for an instantiation.
 * </p>
 *
 * @see #Type()
 * @see #of(Class)
 */
@SuppressWarnings({"AbstractClassWithoutAbstractMethods", "unused"})
public abstract class Type<T> {

    private static final String TO_STRING = "toString";
    private static final String HASH_CODE = "hashCode";

    private final Stage stage;
    private final Supplier<List<Object>> listView = new Lazy<>(this::newListView);
    private final Supplier<Integer> hash = new Lazy<>(this::newHash);
    private final Supplier<String> stringView = new Lazy<>(this::newStringView);

    private List<Object> newListView() {
        return Arrays.asList(getUnderlyingClass(), getActualParameters());
    }

    private Integer newHash() {
        return listView.get().hashCode();
    }

    private String newStringView() {
        return stage.toString();
    }

    /**
     * Initializes a {@link Type} based on its well-defined derivative.
     */
    protected Type() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.stage = TypeVariant.toStage(
                genericSuperclass.getActualTypeArguments()[0],
                ClassVariant.toStage(getClass())
        );
    }

    private Type(final Stage stage) {
        this.stage = stage;
    }

    /**
     * Returns a {@link Type} based on a simple {@link Class}.
     */
    public static <T> Type<T> of(final Class<T> simpleClass) {
        return new Type<T>(ClassVariant.toStage(simpleClass)) {
        };
    }

    static Type<?> of(final Stage stage) {
        return new Type(stage) {
        };
    }

    /**
     * Returns the {@link Class} on which this Type is based.
     */
    public final Class<?> getUnderlyingClass() {
        return stage.getUnderlyingClass();
    }

    /**
     * Returns the formal type parameter of the generic type underlying this Type.
     *
     * @see #getActualParameters()
     */
    public final List<String> getFormalParameters() {
        return stage.getFormalParameters();
    }

    /**
     * <p>Returns the actual type parameters defining this Type.</p>
     * <p>The result may be empty though the formal parameter list is not empty. Otherwise (in most cases) the formal
     * and actual parameter list are of the1 same size and order.</p>
     *
     * @see #getFormalParameters()
     */
    public final List<Type<?>> getActualParameters() {
        return stage.getActualParameters();
    }

    /**
     * Converts a (possibly generic) {@link java.lang.reflect.Type} that exists in the
     * {@linkplain #getUnderlyingClass() underlying class} of this Type into a definite Type (like this).
     *
     * @see Class#getGenericSuperclass()
     * @see Class#getGenericInterfaces()
     * @see Class#getFields()
     * @see Class#getMethods()
     * @see Field#getGenericType()
     * @see Method#getGenericReturnType()
     * @see Method#getGenericParameterTypes()
     */
    public final Type<?> getMemberType(final java.lang.reflect.Type type) {
        return new Type(TypeVariant.toStage(type, stage)) {
        };
    }

    /**
     * {@inheritDoc}
     * <p>
     * Two instances of Type are equal if they are {@linkplain #getUnderlyingClass() based} on the same class
     * and defined by the same {@linkplain #getActualParameters() actual parameters}.
     * </p>
     */
    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof Type) && isEqual((Type<?>) obj));
    }

    private boolean isEqual(final Type<?> other) {
        return listView.get().equals(other.listView.get());
    }

    @Override
    public final int hashCode() {
        return hash.get();
    }

    @Override
    public final String toString() {
        return stringView.get();
    }
}
