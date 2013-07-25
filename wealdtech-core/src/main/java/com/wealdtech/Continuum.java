/*
 * Copyright 2013 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import com.google.common.collect.*;

import static com.wealdtech.Preconditions.checkNotNull;
import static com.wealdtech.Preconditions.checkState;

/**
 * A continuum is a ranged value with discrete groupings within set ranges.  It is similar to an enum except there is also a set
 * range of values associated with each enum to allow for quantitative difference between items with the same base type.
 * <p/>
 * A continuum is useful where something like an enum is useful but does not provide quite enough detail.  An example of this would
 * be prioritised attendance levels, where there are discrete levels such as 'like to attend' and 'must attend' but there needs to
 * be a way of ordering multiple 'like to attend' items without creating an arbitrary number of enums.
 * <p/>
 * To create your own continuum simply subclass this and set your own values for {@code levels} and {@elements}
 */
public class Continuum
{
  protected static final transient Range<Integer> levels;
  protected static final transient ImmutableList<String> elements;
  private static final transient int levelSize;

  static
  {
    levels = Range.closedOpen(-50, 50);
    elements = ImmutableList.of("One", "Two", "Three");
    levelSize = ContiguousSet.create(levels, DiscreteDomain.integers()).size();
  }

  private final int element;
  private final int level;

  public static Continuum fromInt(final Integer value)
  {
    final int element = value / levelSize;
    checkState(elements.size() > element, "Value too large for continuum");
    final int level = value + levels.lowerEndpoint() - (element * levelSize);
    return new Continuum(element, level);
  }

  public static Continuum fromString(final String value)
  {
    return new Continuum(0, 0);
  }

  public Continuum(final int element, final int level)
  {
    checkState(elements.size() > -1 && elements.size() >= element, "Invalid element");
    checkState(levels.contains(level), "Invalid level");
    this.element = element;
    this.level = level;
  }

  public Continuum increment()
  {
    if ((levels.upperBoundType() == BoundType.CLOSED && level == levels.upperEndpoint()) ||
        (levels.upperBoundType() == BoundType.OPEN && level == levels.upperEndpoint() - 1))
    {
      // Crossed a boundary, go to the next element
      if (levels.lowerBoundType() == BoundType.CLOSED)
      {
        return new Continuum(element + 1, levels.lowerEndpoint());
      }
      else
      {
        return new Continuum(element + 1, levels.lowerEndpoint() + 1);
      }
    }
    else
    {
      // Increment level in the current element
      return new Continuum(element, level + 1);
    }
  }

  public Continuum decrement()
  {
    if ((levels.lowerBoundType() == BoundType.CLOSED && level == levels.lowerEndpoint()) ||
        (levels.lowerBoundType() == BoundType.OPEN && level == levels.lowerEndpoint() + 1))
    {
      // Crossed a boundary, go to the previous element
      if (levels.upperBoundType() == BoundType.CLOSED)
      {
        return new Continuum(element - 1, levels.upperEndpoint());
      }
      else
      {
        return new Continuum(element - 1, levels.upperEndpoint() - 1);
      }
    }
    else
    {
      // Decrement level in the current element
      return new Continuum(element, level - 1);
    }
  }

  public int getElement()
  {
    return element;
  }

  public int getLevel()
  {
    return level;
  }

  public String toString()
  {
    final StringBuilder sb = new StringBuilder(32);
    sb.append(elements.get(element));
    sb.append(" (");
    sb.append(level);
    sb.append(')');
    return sb.toString();
  }
}
