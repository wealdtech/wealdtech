/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.utils;

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;

public class GuavaUtils
{
  public static <T extends Collection<?>> T emptyToNull(final T cl)
  {
    if (cl == null || cl.isEmpty())
    {
      return null;
    }
    return cl;
  }

  public static <T, U> Map<T, U> emptyToNull(final Map<T, U> input)
  {
    if (input == null || input.isEmpty())
    {
      return null;
    }
    return input;
  }

  public static <T, U> Multimap<T, U> emptyToNull(final Multimap<T, U> input)
  {
    if (input == null || input.isEmpty())
    {
      return null;
    }
    return input;
  }

}
