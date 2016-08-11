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

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import static com.wealdtech.Preconditions.*;

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

  @Override public T orNull() {
    return reference;
  }

  @Override public Set<T> asSet() {
    return Collections.singleton(reference);
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
