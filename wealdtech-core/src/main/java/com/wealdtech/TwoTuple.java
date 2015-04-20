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
 * A simple two-tuple, consisting of two separate values.
 */
public class TwoTuple<S, T>
{
  private final S s;
  private final T t;

  /**
   * Obtain the first value in a two-tuple.
   * @return The first value in the two-tuple.
   */
  public S getS()
  {
    return s;
  }

  /**
   * Obtain the second value in a two-tuple.
   * @return The second value in the two-tuple.
   */
  public T getT()
  {
    return t;
  }

  /**
   * Create a two-tuple.
   * @param s the first item in the tuple
   * @param t the second item in the tuple
   */
  @JsonCreator
  public TwoTuple(@JsonProperty("s") final S s,
                  @JsonProperty("t") final T t)
  {
      this.s = s;
      this.t = t;
  }

  // Standard object methods follow
  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this).add("s", this.s).add("t", this.t).omitNullValues().toString();
  }

  @Override
  public boolean equals(final Object that)
  {
    if (that == null)
    {
      return false;
    }
    if (!(that instanceof TwoTuple))
    {
      return false;
    }
    @SuppressWarnings("unchecked")
    final TwoTuple<S, T>tthat = (TwoTuple<S, T>)that;
    return Objects.equal(this.s, tthat.s) && Objects.equal(this.t, tthat.t);
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(this.s,
                            this.t);
  }
}
