/*
 * Copyright 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.utils;

import com.google.common.collect.Range;
import com.wealdtech.DataError;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.Locale;

/**
 * Formatter for closedOPen {@link org.joda.time.DateTime} {@link com.google.common.collect.Range} objects, taking in to account
 * sane defaults and localisation
 */
public class RangeFormatter
{
  private final Locale locale;
  private final boolean shortForm;

  public RangeFormatter()
  {
    this.locale = Locale.getDefault();
    this.shortForm = true;
  }

  public RangeFormatter(final boolean shortForm)
  {
    this.locale = Locale.getDefault();
    this.shortForm = shortForm;
  }

  public RangeFormatter(final Locale locale, final boolean shortForm)
  {
    this.locale = locale;
    this.shortForm = shortForm;
  }

  /**
   * Format the date and time of a date/time range.
   * @param range the range to format
   * @return a formatted range
   */
  public String formatDateTime(final Range<DateTime> range)
  {
    final DateTime curDateTime = DateTime.now();
    final StringBuilder sb = new StringBuilder(96);

    final DateTime lower = range.lowerEndpoint();
    final DateTime upper = range.upperEndpoint();
    if (upper.isBefore(lower))
    {
      throw new DataError.Bad("Upper part of range must be after lower part of range");
    }
    // Lower date
    final Details lowerDetails = new Details();
    lowerDetails.showTime = true;
    if (!isSameDay(lower, curDateTime))
    {
      lowerDetails.showDayOfWeek = true;
      lowerDetails.showDayOfMonth = true;
      lowerDetails.showMonthOfYear = true;
    }
    if ((!isSameYear(lower, upper)) ||
        (!isSameYear(lower, curDateTime)))
    {
      lowerDetails.showYear = true;
    }
    sb.append(doFormat(lower, lowerDetails));

    if (!isSameMinute(lower, upper))
    {
      sb.append(" - ");

      final Details upperDetails = new Details();
      upperDetails.showTime = true;
      if (!isSameDay(lower, upper))
      {
        upperDetails.showDayOfWeek = true;
        upperDetails.showDayOfMonth = true;
        upperDetails.showMonthOfYear = true;
      }
      if ((!isSameYear(lower, upper)) ||
          (!isSameYear(upper, curDateTime)))
      {
        upperDetails.showYear = true;
      }

      sb.append(doFormat(upper, upperDetails));
    }
    return sb.toString();
  }

  /**
   * Format the dates of a date/time range.
   * @param range the range to format
   * @return the formatted range
   */
  public String formatDate(final Range<DateTime> range)
  {
    final DateTime curDateTime = DateTime.now();
    final StringBuilder sb = new StringBuilder(64);

    // Dates.  Note that because we are working with dates and dates are closed/open we need to take a day away from the upper
    // date to make the format look right
    final DateTime lower = range.lowerEndpoint();
    final DateTime upper = range.upperEndpoint().minusDays(1);
    if (upper.isBefore(lower))
    {
      throw new DataError.Bad("Upper part of range must be after lower part of range");
    }

    // Lower date
    final Details lowerDetails = new Details();
    lowerDetails.showTime = false;
    lowerDetails.showDayOfWeek = true;
    lowerDetails.showDayOfMonth = true;

    if (!isSameMonth(lower, upper))
    {
      lowerDetails.showMonthOfYear = true;
    }
    if ((!isSameYear(lower, upper)) &&
        (!isSameYear(lower, curDateTime)))
    {
      lowerDetails.showYear = true;
    }
    sb.append(doFormat(lower, lowerDetails));

    if (!isSameDay(lower, upper))
    {
      sb.append(" - ");

      final Details upperDetails = new Details();
      upperDetails.showTime = false;
      upperDetails.showDayOfWeek = true;
      upperDetails.showDayOfMonth = true;
      upperDetails.showMonthOfYear = true;
      if ((!isSameYear(lower, upper)) ||
          (!isSameYear(upper, curDateTime)))
      {
        upperDetails.showYear = true;
      }

      sb.append(doFormat(upper, upperDetails));
    }

    return sb.toString();
  }

  /**
   * Format the date of a single date/time
   * @param dateTime the date/time to format
   * @return  a formatted date
   */
  public String formatDate(final DateTime dateTime)
  {
    return formatDateAndTime(dateTime, false);
  }

  /**
   * Format the date and time of a single date/time
   * @param dateTime the date/time to format
   * @return  a formatted date
   */
  public String formatDateTime(final DateTime dateTime)
  {
    return formatDateAndTime(dateTime, true);
  }

  /**
   * Format the date and time of a single date/time
   * @param dateTime the date/time to format
   * @param showTime show the time as well as the date
   * @return  a formatted date
   */
  private String formatDateAndTime(final DateTime dateTime, final boolean showTime)
  {
    final DateTime curDateTime = DateTime.now();
    final Details dateDetails = new Details();
    dateDetails.showTime = showTime;
    dateDetails.showDayOfWeek = true;
    dateDetails.showDayOfMonth = true;
    dateDetails.showMonthOfYear = true;
    if (!isSameYear(dateTime, curDateTime))
    {
      dateDetails.showYear = true;
    }
    return doFormat(dateTime, dateDetails);
  }

  // Carry out the format
  private String doFormat(final DateTime datetime, final Details formatDetails)
  {
    final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
    boolean started = false;
    if (formatDetails.showDayOfWeek)
    {
      if (shortForm)
      {
        builder.appendDayOfWeekShortText();
      }
      else
      {
        builder.appendDayOfWeekText();
      }
      started = true;
    }
    if (formatDetails.showDayOfMonth)
    {
      if (started)
      {
        builder.appendLiteral(' ');
      }
      builder.appendDayOfMonth(1);
      started = true;
    }
    if (formatDetails.showMonthOfYear)
    {
      if (started)
      {
        builder.appendLiteral(' ');
      }
      if (shortForm)
      {
        builder.appendMonthOfYearShortText();
      }
      else
      {
        builder.appendMonthOfYearText();
      }
      started = true;
    }
    if (formatDetails.showYear)
    {
      if (started)
      {
        builder.appendLiteral(' ');
      }
      builder.appendYear(4, 4);
      started = true;
    }
    if (formatDetails.showTime)
    {
      if (started)
      {
        builder.appendLiteral(' ');
      }
      builder.appendHourOfDay(2);
      builder.appendLiteral(':');
      builder.appendMinuteOfHour(2);
    }

    return datetime.toString(builder.toFormatter());
  }

  private boolean isSameMinute(final DateTime lower, final DateTime upper)
  {
    return (lower.getYear() == upper.getYear()) &&
           (lower.getMonthOfYear() == upper.getMonthOfYear()) &&
           (lower.getDayOfYear() == upper.getDayOfYear()) &&
           (lower.getHourOfDay() == upper.getHourOfDay()) &&
           (lower.getMinuteOfHour() == upper.getMinuteOfHour());
  }
  private boolean isSameHour(final DateTime lower, final DateTime upper)
  {
    return (lower.getYear() == upper.getYear()) &&
           (lower.getMonthOfYear() == upper.getMonthOfYear()) &&
           (lower.getDayOfYear() == upper.getDayOfYear()) &&
           (lower.getHourOfDay() == upper.getHourOfDay());
  }
  private boolean isSameDay(final DateTime lower, final DateTime upper)
  {
    return (lower.getYear() == upper.getYear()) &&
           (lower.getMonthOfYear() == upper.getMonthOfYear()) &&
           (lower.getDayOfYear() == upper.getDayOfYear());
  }
  private boolean isSameMonth(final DateTime lower, final DateTime upper)
  {
    return (lower.getYear() == upper.getYear()) &&
           (lower.getMonthOfYear() == upper.getMonthOfYear());
  }
  private boolean isSameYear(final DateTime lower, final DateTime upper)
  {
    return (lower.getYear() == upper.getYear());
  }

  private static class Details
  {
    public boolean showTime = false;
    public boolean showDayOfWeek = false;
    public boolean showDayOfMonth = false;
    public boolean showMonthOfYear = false;
    public boolean showYear = false;
  }
}
