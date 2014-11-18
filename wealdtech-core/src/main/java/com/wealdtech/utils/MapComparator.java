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


import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;

/**
 * Comparator for maps.
 */
public class MapComparator<S extends Comparable<S>, T> implements Comparator<Map<S, T>>
{
  @SuppressWarnings("unchecked")
  @Override
  public int compare(@Nullable final Map<S, T> first, @Nullable final Map<S, T> second)
  {
    int result = 0;
    if (first == null  && second == null)
    {
      result = 0;
    }
    else if (first == null)
    {
      result = -1;
    }
    else if (second == null)
    {
      result = 1;
    }
    else
    {
      for (final Map.Entry<S, T> firstEntry : first.entrySet())
      {
        final T secondValue = second.get(firstEntry.getKey());
        if (secondValue == null)
        {
          result = 1;
          break;
        }
        else
        {
          if (firstEntry.getValue() instanceof Comparable &&
              secondValue instanceof Comparable &&
              firstEntry.getValue().getClass().equals(secondValue.getClass()))
          {
            result = ((Comparable)firstEntry.getValue()).compareTo(secondValue);
          }
          else
          {
            result = firstEntry.getValue().toString().compareTo(secondValue.toString());
          }
          if (result != 0)
          {
            break;
          }
        }
      }
    }
    return result;
  }
}
