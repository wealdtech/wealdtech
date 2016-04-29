/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.rekt;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

public class Value<T>
{
  private final Class<T> type;
  private final String name;
  private final String input;
  private final ImmutableList<T> potentialValues;
  private final T value;
  private final State state;

  public Value(final Class<T> type,
               final String name,
               final String input,
               @Nullable final ImmutableList<T> potentialValues,
               @Nullable final T value,
               final State state)
  {
    this.type = type;
    this.name = name;
    this.input = input;
    this.potentialValues = potentialValues;
    this.value = value;
    this.state = state;
  }

  public Class<T> getType() { return type; }

  public String getName()
  {
    return name;
  }

  public String getInput()
  {
    return input;
  }

  @Nullable
  public ImmutableList<T> getPotentialValues()
  {
    return potentialValues;
  }

  @Nullable
  public T getValue()
  {
    return value;
  }

  public State getState()
  {
    return state;
  }

  public static enum State
  {
    NOT_PRESENT,
    NOT_PARSED,
    AMBIGUOUS,
    INVALID,
    VALID;
  }
}

