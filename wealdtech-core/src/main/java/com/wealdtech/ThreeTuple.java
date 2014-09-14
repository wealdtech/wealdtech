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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * A simple three-tuple, consisting of three separate values.
 */
public class ThreeTuple<S, T, U>
{
  private final S s;
  private final T t;
  private final U u;

  /**
   * Obtain the first value in a three-tuple.
   *
   * @return The first value in the three-tuple.
   */
  public S getS()
  {
    return s;
  }

  /**
   * Obtain the second value in a three-tuple.
   *
   * @return The second value in the three-tuple.
   */
  public T getT()
  {
    return t;
  }

  /**
   * Obtain the third value in a three-tuple.
   *
   * @return The third value in the three-tuple.
   */
  public U getU()
  {
    return u;
  }

  /**
   * Create a three-tuple.
   *
   * @param s the first item in the tuple
   * @param t the second item in the tuple
   * @param u the third item in the tuple
   */
  @JsonCreator
  public ThreeTuple(@JsonProperty("s") final S s, @JsonProperty("t") final T t, @JsonProperty("u") final U u)
  {
    this.s = s;
    this.t = t;
    this.u = u;
  }

  // Standard object methods follow
  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this).add("s", this.s).add("t", this.t).add("u", this.u).omitNullValues().toString();
  }

  @Override
  public boolean equals(final Object that)
  {
    if (that == null)
    {
      return false;
    }
    if (!(that instanceof ThreeTuple))
    {
      return false;
    }
    @SuppressWarnings("unchecked")
    final ThreeTuple<S, T, U> tthat = (ThreeTuple<S, T, U>)that;
    return Objects.equal(this.s, tthat.s) && Objects.equal(this.t, tthat.t) && Objects.equal(this.u, tthat.u);
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(this.s, this.t, this.u);
  }
}
