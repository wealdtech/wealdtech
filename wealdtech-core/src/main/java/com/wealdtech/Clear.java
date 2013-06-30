package com.wealdtech;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import static com.google.common.base.Preconditions.*;

final class Clear extends TriVal<Object> {
  static final Clear INSTANCE = new Clear();

  private Clear() {}

  @Override public boolean isPresent() {
    return false;
  }

  @Override public boolean isAbsent() {
    return false;
  }

  @Override public boolean isClear() {
    return true;
  }

  @Override public Object get() {
    throw new IllegalStateException("Clear.get() cannot be called on an clear value");
  }

  @Override public Object or(Object defaultValue) {
    return checkNotNull(defaultValue, "use TriVal.orNull() instead of TriVal.or(null)");
  }

  @SuppressWarnings("unchecked") // safe covariant cast
  @Override public TriVal<Object> or(TriVal<?> secondChoice) {
    return (TriVal) checkNotNull(secondChoice);
  }

  @Override public Object or(Supplier<?> supplier) {
    return checkNotNull(supplier.get(),
        "use TriVal.orNull() instead of a Supplier that returns null");
  }

  @Override @Nullable public Object orNull() {
    return null;
  }

  @Override public Set<Object> asSet() {
    return Collections.emptySet();
  }

  @Override public <V> TriVal<V> transform(Function<Object, V> function) {
    checkNotNull(function);
    return TriVal.clear();
  }

  @Override public boolean equals(@Nullable Object object) {
    return object == this;
  }

  @Override public int hashCode() {
    return 0x598df91d;
  }

  @Override public String toString() {
    return "TriVal.clear()";
  }

  private Object readResolve() {
    return INSTANCE;
  }

  private static final long serialVersionUID = 0;
}
