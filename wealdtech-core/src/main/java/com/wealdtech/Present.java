package com.wealdtech;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import static com.google.common.base.Preconditions.*;

final class Present<T> extends TriVal<T> {
  private final T reference;

  Present(T reference) {
    this.reference = reference;
  }

  @Override public boolean isPresent() {
    return true;
  }

  @Override public boolean isAbsent() {
    return false;
  }

  @Override public boolean isClear() {
    return false;
  }

  @Override public T get() {
    return reference;
  }

  @Override public T or(T defaultValue) {
    checkNotNull(defaultValue, "use TriVal.orNull() instead of TriVal.or(null)");
    return reference;
  }

  @Override public TriVal<T> or(TriVal<? extends T> secondChoice) {
    checkNotNull(secondChoice);
    return this;
  }

  @Override public T or(Supplier<? extends T> supplier) {
    checkNotNull(supplier);
    return reference;
  }

  @Override public T orNull() {
    return reference;
  }

  @Override public Set<T> asSet() {
    return Collections.singleton(reference);
  }

  @Override public <V> TriVal<V> transform(Function<? super T, V> function) {
    return new Present<V>(checkNotNull(function.apply(reference),
        "the Function passed to TriVal.transform() must not return null."));
  }

  @Override public boolean equals(@Nullable Object object) {
    if (object instanceof Present) {
      Present<?> other = (Present<?>) object;
      return reference.equals(other.reference);
    }
    return false;
  }

  @Override public int hashCode() {
    return 0x598df91c + reference.hashCode();
  }

  @Override public String toString() {
    return "TriVal.of(" + reference + ")";
  }

  @Override public String toSimpleString() {
    return reference.toString();
  }

  private static final long serialVersionUID = 0;
}
