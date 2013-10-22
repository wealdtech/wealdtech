/*
 * Copyright 2013 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.utils;

import com.google.common.collect.Range;

import java.util.Comparator;

/**
 * Comparator for Guava {@link Range} types.
 *
 * The greater range is defined using the following criteria, in order:
 * <ul>
 *   <li>The ones with the lowest lower bound.
 *     <ul>
 *       <li>An unbounded item is considered less than a bounded item</li>
 *       <li>A closed bound is considered less than an open bound</li>
 *     </ul>
 *   </li>
 *   <li>The ones with the highest upper bound.
 *     <ul>
 *       <li>An unbounded item is considered more than a bounded item</li>
 *       <li>A closed bound is considered more than an open bound</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public class RangeComparator<C extends Comparable<C>> implements Comparator<Range<C>>
{
  @Override
  public int compare(Range<C> range1, Range<C> range2)
  {
    int result = ((Boolean)range1.hasLowerBound()).compareTo(range2.hasLowerBound());
    if (result == 0)
    {
      if (range1.hasLowerBound())
      {
       result = range1.lowerEndpoint().compareTo(range2.lowerEndpoint());
        if (result == 0)
        {
          result = range1.lowerBoundType().compareTo(range2.lowerBoundType());
        }
      }
    }

    if (result == 0)
    {
      result = ((Boolean)range1.hasUpperBound()).compareTo(range2.hasUpperBound());
      if (result == 0)
      {
        if (range1.hasUpperBound())
        {
          result = range1.upperEndpoint().compareTo(range2.upperEndpoint());
          if (result == 0)
          {
            result = range1.upperBoundType().compareTo(range2.upperBoundType());
          }
        }
      }
    }

    return result;
  }
}
