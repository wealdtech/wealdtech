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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

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
    return (TriVal<Object>) checkNotNull(secondChoice);
  }

  @Override @Nullable public Object orNull() {
    return null;
  }

  @Override public Set<Object> asSet() {
    return Collections.emptySet();
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

  @Override public String toSimpleString() {
    return "<clear>";
  }

  private Object readResolve() {
    return INSTANCE;
  }

  private static final long serialVersionUID = 0;
}
