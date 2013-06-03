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

import org.joda.time.Chronology;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationFieldType;
import org.joda.time.field.DividedDateTimeField;
import org.joda.time.field.OffsetDateTimeField;

/**
 * Utilities for use with Joda time
 */
public class Joda
{
  /**
   * A {@link DateTimeFieldType} using the notion of absolute week of the month.
   * <p/>The absolute week of the month is derived directly from the day of the
   * month, with the first seven days of any given month being in the first week,
   * the second seven days being in the second week, <em>etc.</em>.
   */
  public static final DateTimeFieldType AbsWeekOfMonth = new DateTimeFieldType("absWeekOfMonth")
  {
    private static final long serialVersionUID = 1805873429002528912L;

    @Override
    public DurationFieldType getDurationType()
    {
      return DurationFieldType.weeks();
    }

    @Override
    public DurationFieldType getRangeDurationType()
    {
      return DurationFieldType.months();
    }

    @Override
    public DateTimeField getField(Chronology chronology)
    {
      return new OffsetDateTimeField(new DividedDateTimeField(new OffsetDateTimeField(chronology.dayOfMonth(), -1), AbsWeekOfMonth, DateTimeConstants.DAYS_PER_WEEK), 1);
    }

    @Override
    public boolean equals(final Object that)
    {
      return (this == that);
    }

    @Override
    public int hashCode()
    {
      return super.hashCode();
    }
  };

  /**
   * A {@link DateTimeFieldType} using the notion of absolute week of the year.
   * <p/>The absolute week of the year is derived directly from the day of the
   * year, with the first seven days of any given year being in the first week,
   * the second seven days being in the second week, <em>etc.</em>.
   */
  public static final DateTimeFieldType AbsWeekOfYear = new DateTimeFieldType("absWeekOfYear")
  {
    private static final long serialVersionUID = 3346155277569481686L;

    @Override
    public DurationFieldType getDurationType()
    {
      return DurationFieldType.weeks();
    }

    @Override
    public DurationFieldType getRangeDurationType()
    {
      return DurationFieldType.years();
    }

    @Override
    public DateTimeField getField(Chronology chronology)
    {
      return new OffsetDateTimeField(new DividedDateTimeField(new OffsetDateTimeField(chronology.dayOfYear(), -1), AbsWeekOfYear, DateTimeConstants.DAYS_PER_WEEK), 1);
    }

    @Override
    public boolean equals(final Object that)
    {
      return (this == that);
    }

    @Override
    public int hashCode()
    {
      return super.hashCode();
    }
  };
}
