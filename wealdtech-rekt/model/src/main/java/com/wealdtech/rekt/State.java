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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.wealdtech.DataError;
import com.wealdtech.utils.StringUtils;

import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;

public enum State
{
  /**
   * No value supplied
   */
  MISSING(0),
  /**
   * Value supplied cannot be understood
   */
  UNPARSEABLE(1),
  /**
   * Value supplied results in multiple possible answers
   */
  AMBIGUOUS(2),
  /**
   * Value supplied does not meet validation requirements
   */
  INVALID(3),
  /**
   * Value supplie is good
   */
  GOOD(4);

  private static final ImmutableSortedMap<Integer, State> _VALMAP;

  static
  {
    final Map<Integer, State> levelMap = Maps.newHashMap();
    for (final State contextType : State.values())
    {
      levelMap.put(contextType.val, contextType);
    }
    _VALMAP = ImmutableSortedMap.copyOf(levelMap);
  }

  public final int val;

  private State(final int val)
  {
    this.val = val;
  }

  @JsonCreator
  public static State fromString(final String val)
  {
    try
    {
      return valueOf(val.toUpperCase(Locale.ENGLISH).replaceAll(" ", "_"));
    }
    catch (final IllegalArgumentException iae)
    {
      // N.B. we don't pass the iae as the cause of this exception because
      // this happens during invocation, and in that case the enum handler
      // will report the root cause exception rather than the one we throw.
      throw new DataError.Bad("A state \"" + val + "\" supplied is invalid");
    }
  }

  public static State fromInt(final Integer val)
  {
    checkNotNull(val, "State not supplied");
    final State state = _VALMAP.get(val);
    checkNotNull(state, "State is invalid");
    return state;
  }

  @Override
  @JsonValue
  public String toString()
  {
    return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH)).replaceAll("_", " ");
  }
}
