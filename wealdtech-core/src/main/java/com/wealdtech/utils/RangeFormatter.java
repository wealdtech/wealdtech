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

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Formatter for closedOPen {@link org.joda.time.DateTime} {@link com.google.common.collect.Range} objects, taking in to account
 * sane defaults and localisation
 */
public class RangeFormatter
{
  public enum Style
  {
    FULL,
    NORMAL,
    TIME_ONLY
  }

  private final Locale locale;
  private Style style;

  public RangeFormatter()
  {
    this.locale = Locale.getDefault();
    this.style = Style.NORMAL;
  }

  public RangeFormatter(final Style style)
  {
    this.locale = Locale.getDefault();
    this.style = style;
  }

  public RangeFormatter(final Locale locale, final Style style)
  {
    this.locale = locale;
    this.style = style;
  }

  /**
   * Format the date and time of a date/time range.
   *
   * @param range the range to format
   * @return a formatted range, or {@code null} if the input is {@code null}
   */
  @Nullable
  public String formatDateTime(@Nullable final Range<DateTime> range)
  {
    if (range == null)
    {
      return null;
    }
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
    if ((!isSameYear(lower, upper)) || (!isSameYear(lower, curDateTime)))
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
        if ((!isSameYear(lower, upper)) || (!isSameYear(upper, curDateTime)))
        {
          upperDetails.showYear = true;
        }
      }

      sb.append(doFormat(upper, upperDetails));
    }
    return sb.toString();
  }

  /**
   * Format the dates of a date/time range.
   *
   * @param range the range to format
   * @return the formatted range, or {@code null} if the input is {@code null}
   */
  @Nullable
  public String formatDate(@Nullable final Range<DateTime> range)
  {
    if (range == null || style == Style.TIME_ONLY)
    {
      return null;
    }

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

    final boolean singleDay = isSameDay(lower, upper);

    // Lower date
    final Details lowerDetails = new Details();
    lowerDetails.showTime = false;
    lowerDetails.showDayOfWeek = true;
    lowerDetails.showDayOfMonth = true;

    if (!isSameMonth(lower, upper) || singleDay)
    {
      lowerDetails.showMonthOfYear = true;
    }
    if ((!isSameYear(lower, curDateTime)) && (singleDay || !isSameYear(lower, upper)))
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
      if ((!isSameYear(lower, upper)) || (!isSameYear(upper, curDateTime)))
      {
        upperDetails.showYear = true;
      }

      sb.append(doFormat(upper, upperDetails));
    }
    else
    {
      // Need to append month and year if applicable
      if (!isSameMonth(lower, curDateTime))
      {
        lowerDetails.showMonthOfYear = true;
      }
      if (!isSameYear(lower, curDateTime))
      {
        lowerDetails.showYear = true;
      }
    }

    return sb.toString();
  }

  /**
   * Format the date of a single date/time
   *
   * @param dateTime the date/time to format
   * @return a formatted date, or {@code null} if the input is {@code null}
   */
  @Nullable
  public String formatDate(@Nullable final DateTime dateTime)
  {
    return dateTime == null || style == Style.TIME_ONLY ? null : formatDateAndTime(dateTime, true, false);
  }

  /**
   * Format the time of a single date/time
   *
   * @param dateTime the date/time to format
   * @return a formatted time, or {@code null} if the input is {@code null}
   */
  @Nullable
  public String formatTime(@Nullable final DateTime dateTime)
  {
    return dateTime == null ? null : formatDateAndTime(dateTime, false, true);
  }


  /**
   * Format the date and time of a single date/time
   *
   * @param dateTime the date/time to format
   * @return a formatted date, or {@code null} if the input is {@code null}
   */
  @Nullable
  public String formatDateTime(@Nullable final DateTime dateTime)
  {
    return dateTime == null ? null : formatDateAndTime(dateTime, true, true);
  }

  /**
   * Format the date and time of a single date/time
   *
   * @param dateTime the date/time to format
   * @param showTime show the time as well as the date
   * @return a formatted date
   */
  private String formatDateAndTime(final DateTime dateTime, final boolean showDate, final boolean showTime)
  {
    final DateTime curDateTime = DateTime.now();
    final Details dateDetails = new Details();
    dateDetails.showTime = showTime;
    dateDetails.showDayOfWeek = showDate;
    dateDetails.showDayOfMonth = showDate;
    dateDetails.showMonthOfYear = showDate;
    if (!isSameYear(dateTime, curDateTime))
    {
      dateDetails.showYear = showDate;
    }
    return doFormat(dateTime, dateDetails);
  }

  // Carry out the format
  private String doFormat(final DateTime datetime, final Details formatDetails)
  {
    final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
    boolean started = false;
    if (style != Style.TIME_ONLY)
    {
      if (formatDetails.showDayOfWeek)
      {
        if (style == Style.NORMAL)
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
        if (style == Style.NORMAL)
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
      started = true;
    }

    if (started)
    {
      return datetime.toString(builder.toFormatter().withLocale(locale));
    }
    else
    {
      return null;
    }
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
    return (lower.getYear() == upper.getYear()) && (lower.getMonthOfYear() == upper.getMonthOfYear());
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
