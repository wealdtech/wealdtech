/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wealdtech.DataError;
import com.wealdtech.utils.StringUtils;

import java.util.Locale;

/**
 */
public enum MessageScope
{
  INDIVIDUAL,
  FRIENDS,
  EVERYONE;

  @Override
  @JsonValue
  public String toString()
  {
    return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH).replaceAll("_", " "));
  }

  @JsonCreator
  public static MessageScope fromString(final String val)
  {
    try
    {
      return valueOf(val.trim().toUpperCase(Locale.ENGLISH).replaceAll(" ", "_"));
    }
    catch (final IllegalArgumentException iae)
    {
      // N.B. we don't pass the iae as the cause of this exception because this happens during invocation, and in that case the
      // enum handler will report the root cause exception rather than the one we throw.
      throw new DataError.Bad("A chat scope supplied is invalid");
    }
  }
}
