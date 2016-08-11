/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import static com.wealdtech.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

public abstract class TriVal<T> implements Serializable
{
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
   * Returns a string representation which hides the detail of the trival itself
   */
  public abstract String toSimpleString();

  private static final long serialVersionUID = 0;
}
