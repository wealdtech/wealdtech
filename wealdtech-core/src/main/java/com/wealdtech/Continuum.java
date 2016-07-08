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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;

/**
 * A continuum is a ranged value with discrete groupings within set ranges.  It is similar to an enum except there is also a set
 * range of values associated with each enum to allow for quantitative difference between items with the same base type.
 * <p>
 * A continuum is useful where something like an enum is useful but does not provide quite enough detail.  An example of this would
 * be prioritised attendance levels, where there are discrete levels such as 'like to attend' and 'must attend' but there needs to
 * be a way of ordering multiple 'like to attend' items without creating an arbitrary number of enums.
 * <p>
 * To create your own continuum simply subclass this and set your own values for {@code ranges}
 */
public abstract class Continuum<C extends Comparable<C>> implements Comparable<Continuum<C>>
{
  protected final ImmutableList<TwoTuple<Range<C>, String>> ranges;
  private final C level;

  public Continuum(final ImmutableList<TwoTuple<Range<C>, String>> ranges, final C level)
  {
    this.ranges = ranges;
    this.level = level;
    validate();
  }

  private void validate()
  {

  }

  public String getName()
  {
    String result = null;
    for (final TwoTuple<Range<C>, String> range : this.ranges)
    {
      if (range.getS().contains(this.level))
      {
        result = range.getT();
        break;
      }
    }
    return result;
  }

  public Range<C> getRange()
  {
    Range<C> result = null;
    for (final TwoTuple<Range<C>, String> range : this.ranges)
    {
      if (range.getS().contains(this.level))
      {
        result = range.getS();
        break;
      }
    }
    return result;
  }

  @Override
  public int compareTo(final Continuum<C> that)
  {
    return this.level.compareTo(that.level);
  }
}
