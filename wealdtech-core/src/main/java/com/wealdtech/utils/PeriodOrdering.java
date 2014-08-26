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

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;

import com.google.common.collect.Ordering;

/**
 * Allow ordering of Joda Periods by comparing them against a common base date.
 * Access to this should be through the static INSTANCE value.
 * <p/>Note that as the date chosen as a comparison base is arbitrary there are some
 * situations where the results are open to debate as to their accuracy when considering
 * periods of differing types (<em>e.g.</em> comparing 31 days to 1 month).  The results
 * will, however, be consistent.
 */
public class PeriodOrdering extends Ordering<Period>
{
  public static final PeriodOrdering INSTANCE = new PeriodOrdering();

  private static final ReadableInstant COMPARISONBASE = DateTime.parse("2012-01-01T01:00:00+0000");

  private PeriodOrdering()
  {
    // Stop public creation of instances
  }

  @Override
  public int compare(final Period left, final Period right)
  {
    return left.toDurationFrom(COMPARISONBASE).compareTo(right.toDurationFrom(COMPARISONBASE));
  }
}
