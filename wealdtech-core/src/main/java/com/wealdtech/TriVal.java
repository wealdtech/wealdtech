package com.wealdtech;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;

import static com.wealdtech.Preconditions.*;

public abstract class TriVal<T> implements Serializable
{
  /**
   * Text-based representation of {@code clear} for systems which do not support it
   */
  public final transient static String CLEAR_TEXT = "__clear";

  /**
   * Returns an {@code TriVal} instance to clear.
   */
  @SuppressWarnings("unchecked")
  public static <T> TriVal<T> clear() {
    return (TriVal<T>) Clear.INSTANCE;
  }

  /**
   * Returns an {@code TriVal} instance with no contained reference.
   */
  @SuppressWarnings("unchecked")
  public static <T> TriVal<T> absent() {
    return (TriVal<T>) Absent.INSTANCE;
  }

  /**
   * Returns an {@code TriVal} instance containing the given non-null reference.
   */
  public static <T> TriVal<T> of(T reference) {
    return new Present<T>(checkNotNull(reference));
  }

  /**
   * If {@code nullableReference} is non-null, returns an {@code TriVal} instance containing that
   * reference; otherwise returns {@link TriVal#absent}.
   */
  public static <T> TriVal<T> fromNullable(@Nullable T nullableReference) {
    return (nullableReference == null)
        ? TriVal.<T>absent()
        : new Present<T>(nullableReference);
  }

  TriVal() {}

  /**
   * Returns {@code true} if this holder contains a (non-null) instance.
   */
  public abstract boolean isPresent();

  /**
   * Returns {@code true} if this holder contains is Absent
   */
  public abstract boolean isAbsent();

  /**
   * Returns {@code true} if this holder contains a clear
   */
  public abstract boolean isClear();

  /**
   * Returns the contained instance, which must be present. If the instance might be
   * absent, use {@link #or(Object)} or {@link #orNull} instead.
   *
   * @throws IllegalStateException if the instance is absent ({@link #isPresent} returns
   *     {@code false})
   */
  public abstract T get();

  /**
   * Returns the contained instance if it is present; {@code defaultValue} otherwise. If
   * no default value should be required because the instance is known to be present, use
   * {@link #get()} instead. For a default value of {@code null}, use {@link #orNull}.
   *
   * <p>Note about generics: The signature {@code public T or(T defaultValue)} is overly
   * restrictive. However, the ideal signature, {@code public <S super T> S or(S)}, is not legal
   * Java. As a result, some sensible operations involving subtypes are compile errors:
   * <pre>   {@code
   *
   *   TriVal<Integer> TriValInt = getSomeTriValInt();
   *   Number value = TriValInt.or(0.5); // error
   *
   *   FluentIterable<? extends Number> numbers = getSomeNumbers();
   *   TriVal<? extends Number> first = numbers.first();
   *   Number value = first.or(0.5); // error}</pre>
   *
   * As a workaround, it is always safe to cast an {@code TriVal<? extends T>} to {@code
   * TriVal<T>}. Casting either of the above example {@code TriVal} instances to {@code
   * TriVal<Number>} (where {@code Number} is the desired output type) solves the problem:
   * <pre>   {@code
   *
   *   TriVal<Number> TriValInt = (TriVal) getSomeTriValInt();
   *   Number value = TriValInt.or(0.5); // fine
   *
   *   FluentIterable<? extends Number> numbers = getSomeNumbers();
   *   TriVal<Number> first = (TriVal) numbers.first();
   *   Number value = first.or(0.5); // fine}</pre>
   */
  public abstract T or(T defaultValue);

  /**
   * Returns this {@code TriVal} if it has a value present; {@code secondChoice}
   * otherwise.
   */
  public abstract TriVal<T> or(TriVal<? extends T> secondChoice);

  /**
   * Returns the contained instance if it is present; {@code supplier.get()} otherwise. If the
   * supplier returns {@code null}, a {@link NullPointerException} is thrown.
   *
   * @throws NullPointerException if the supplier returns {@code null}
   */
  @Beta
  public abstract T or(Supplier<? extends T> supplier);

  /**
   * Returns the contained instance if it is present; {@code null} otherwise. If the
   * instance is known to be present, use {@link #get()} instead.
   */
  @Nullable
  public abstract T orNull();

  /**
   * Returns an immutable singleton {@link Set} whose only element is the contained instance
   * if it is present; an empty immutable {@link Set} otherwise.
   *
   * @since 11.0
   */
  public abstract Set<T> asSet();

  /**
   * If the instance is present, it is transformed with the given {@link Function}; otherwise,
   * {@link TriVal#absent} is returned. If the function returns {@code null}, a
   * {@link NullPointerException} is thrown.
   *
   * @throws NullPointerException if the function returns {@code null}
   *
   * @since 12.0
   */
  public abstract <V> TriVal<V> transform(Function<? super T, V> function);

  /**
   * Returns {@code true} if {@code object} is an {@code TriVal} instance, and either
   * the contained references are {@linkplain Object#equals equal} to each other or both
   * are absent. Note that {@code TriVal} instances of differing parameterized types can
   * be equal.
   */
  @Override
  public abstract boolean equals(@Nullable Object object);

  /**
   * Returns a hash code for this instance.
   */
  @Override
  public abstract int hashCode();

  /**
   * Returns a string representation for this instance. The form of this string
   * representation is unspecified.
   */
  @Override
  public abstract String toString();

  /**
   * Returns the value of each present instance from the supplied {@code TriVals}, in order,
   * skipping over occurrences of {@link TriVal#absent}. Iterators are unmodifiable and are
   * evaluated lazily.
   *
   * @since 11.0 (generics widened in 13.0)
   */
  @Beta
  public static <T> Iterable<T> presentInstances(
      final Iterable<? extends TriVal<? extends T>> TriVals) {
    checkNotNull(TriVals);
    return new Iterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return new AbstractIterator<T>() {
          private final Iterator<? extends TriVal<? extends T>> iterator =
              checkNotNull(TriVals.iterator());

          @Override
          protected T computeNext() {
            while (iterator.hasNext()) {
              TriVal<? extends T> TriVal = iterator.next();
              if (TriVal.isPresent()) {
                return TriVal.get();
              }
            }
            return endOfData();
          }
        };
      }
    };
  }

  private static final long serialVersionUID = 0;
}
