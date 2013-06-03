/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.wealdtech.utils;

import org.joda.time.Interval;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * Allow ordering of Joda Intervals.  Access to this should be through
 * the static INSTANCE value.
 */
public class IntervalOrdering extends Ordering<Interval>
{
  public static final IntervalOrdering INSTANCE = new IntervalOrdering();

  private IntervalOrdering()
  {
    // Stop public creation of instances
  }

  @Override
  public int compare(final Interval left, final Interval right)
  {
    return ComparisonChain.start()
                          .compare(left.getStart(), right.getStart())
                          .compare(left.getEnd(), right.getEnd())
                          .result();
  }
}