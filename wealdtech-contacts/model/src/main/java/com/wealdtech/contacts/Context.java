/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.wealdtech.DataError;
import com.wealdtech.utils.StringUtils;

import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;

/**
 * The context of a request: professional, social, familial
 */
public enum Context
{
  /**
   * Undefined context
   */
  UNDEFINED(-1)
  /**
   * Professional context
   */
  , PROFESSIONAL(1)
  /**
   * Social context
   */
  , SOCIAL(2)
  /**
   * Familial context
   */
  , FAMILIAL(3);

  public final int val;

  private Context(final int val)
  {
    this.val = val;
  }

  private static final ImmutableSortedMap<Integer, Context> _VALMAP;

  static
  {
    final Map<Integer, Context> levelMap = Maps.newHashMap();
    for (final Context relationshipType : Context.values())
    {
      levelMap.put(relationshipType.val, relationshipType);
    }
    _VALMAP = ImmutableSortedMap.copyOf(levelMap);
  }

  @Override
  @JsonValue
  public String toString()
  {
    return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH)).replaceAll("_", " ");
  }

  @JsonCreator
  public static Context fromString(final String val)
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
      throw new DataError.Bad("A context \"" + val + "\" supplied is invalid");
    }
  }

  public static Context fromInt(final Integer val)
  {
    checkNotNull(val, "Context not supplied");
    final Context state = _VALMAP.get(val);
    checkNotNull(state, "Context is invalid");
    return state;
  }
}