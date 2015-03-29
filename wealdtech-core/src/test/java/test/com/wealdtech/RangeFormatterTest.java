/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.wealdtech.utils.RangeFormatter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.testng.Assert.assertEquals;


public class RangeFormatterTest
{
  private static final int thisYear = new DateTime().getYear();
  private static DateTime testDateTime1 = new DateTime(thisYear, 3, 1, 0, 0, DateTimeZone.UTC);
  private static DateTime testDateTime2 = new DateTime(thisYear, 3, 1, 1, 0, DateTimeZone.UTC);
  private static Range<DateTime> testDateTimeRange1 = Range.closedOpen(testDateTime1, testDateTime2);

  private static DateTime testDateTime3 = new DateTime(thisYear - 1, 4, 2, 6, 0, DateTimeZone.UTC);
  private static DateTime testDateTime4 = new DateTime(thisYear - 1, 4, 4, 15, 0, DateTimeZone.UTC);
  private static Range<DateTime> testDateTimeRange2 = Range.closedOpen(testDateTime3, testDateTime4);

  private static DateTime testDateTime5 = new DateTime(thisYear - 1, 3, 2, 8, 0, DateTimeZone.forID("Asia/Tokyo"));
  private static DateTime testDateTime6 = new DateTime(thisYear - 1, 3, 3, 9, 0, DateTimeZone.forID("Asia/Tokyo"));
  private static Range<DateTime> testDateTimeRange3 = Range.closedOpen(testDateTime5, testDateTime6);

  private static DateTime testDateTime7 = new DateTime(thisYear - 1, 4, 30, 12, 0, DateTimeZone.forID("Asia/Tokyo"));
  private static DateTime testDateTime8 = new DateTime(thisYear - 1, 6, 1, 9, 0, DateTimeZone.forID("Asia/Tokyo"));
  private static Range<DateTime> testDateTimeRange4 = Range.closedOpen(testDateTime7, testDateTime8);

  private static DateTime testDateTime9 = new DateTime(thisYear - 2, 4, 30, 12, 0, DateTimeZone.forID("Asia/Tokyo"));
  private static DateTime testDateTime10 = new DateTime(thisYear - 1, 6, 1, 9, 0, DateTimeZone.forID("Asia/Tokyo"));
  private static Range<DateTime> testDateTimeRange5 = Range.closedOpen(testDateTime9, testDateTime10);

  private static final DateTimeFormatter shortDayFmt = new DateTimeFormatterBuilder().appendDayOfWeekShortText().toFormatter();
  private static final DateTimeFormatter longDayFmt = new DateTimeFormatterBuilder().appendDayOfWeekText().toFormatter();
  private static final DateTimeFormatter shortMonthFmt = new DateTimeFormatterBuilder().appendMonthOfYearShortText().toFormatter();
  private static final DateTimeFormatter timeFmt = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).toFormatter();
  private static final DateTimeFormatter yearFmt = new DateTimeFormatterBuilder().appendYear(4, 4).toFormatter();

  @Test
  public void testSingleDate()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final String day = testDateTime1.toString(shortDayFmt);
    assertEquals(formatter.formatDate(testDateTime1), day + " 1 Mar");
  }

  @Test
  public void testSingleDateLongForm()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.FULL);
    final String day = testDateTime1.toString(longDayFmt);
    assertEquals(formatter.formatDate(testDateTime1), day + " 1 March");
  }

  @Test
  public void testSingleDateDifferentYear()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final String day = testDateTime5.toString(shortDayFmt);
    assertEquals(formatter.formatDate(testDateTime5), day + " 2 Mar " + Long.toString(thisYear - 1));
  }

  @Test
  public void testSingleDateDifferentYearLongForm()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.FULL);
    final String day = testDateTime5.toString(longDayFmt);
    assertEquals(formatter.formatDate(testDateTime5), day + " 2 March " + Long.toString(thisYear - 1));
  }

  @Test
  public void testSingleDateTime()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final String day = testDateTime1.toString(shortDayFmt);
    assertEquals(formatter.formatDateTime(testDateTime1), day + " 1 Mar 00:00");
  }

  @Test
  public void testSingleDateNonDefaultLocale()
  {
    final RangeFormatter formatter = new RangeFormatter(Locale.FRANCE, RangeFormatter.Style.NORMAL);
    final DateTimeFormatter frShortDayFmt = new DateTimeFormatterBuilder().appendDayOfWeekShortText().toFormatter().withLocale(Locale.FRANCE);
    final String day = testDateTime1.toString(frShortDayFmt);
    assertEquals(formatter.formatDateTime(testDateTime1), day + " 1 mars 00:00");
  }

  @Test
  public void testSingleDateTimeLongForm()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.FULL);
    final String day = testDateTime1.toString(longDayFmt);
    assertEquals(formatter.formatDateTime(testDateTime1), day + " 1 March 00:00");
  }

  @Test
  public void testSingleDateTimeDifferentYear()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final String day = testDateTime5.toString(shortDayFmt);
    assertEquals(formatter.formatDateTime(testDateTime5), day + " 2 Mar " + Long.toString(thisYear - 1) + " 08:00");
  }

  @Test
  public void testSingleDateTimeDifferentYearLongForm()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.FULL);
    final String day = testDateTime5.toString(longDayFmt);
    assertEquals(formatter.formatDateTime(testDateTime5), day + " 2 March " + Long.toString(thisYear - 1) + " 08:00");
  }

  @Test
  public void testSingleTime()
  {
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatTime(testDateTime3), "06:00");
  }

  @Test
  public void testSingleDateTimeTimeAndDuration()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.TIME_AND_DURATION);
    assertEquals(formatter.formatDateTime(testDateTime3), "06:00");
  }

  @Test
  public void testSingleTimeNonDefaultLocale()
  {
    final RangeFormatter formatter = new RangeFormatter(Locale.CANADA, RangeFormatter.Style.NORMAL);
    assertEquals(formatter.formatTime(testDateTime3), "06:00");
  }

  @Test
  public void testZeroRangeTodayDayDuration()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.TIME_AND_DURATION);
    final Range<DateTime> testRange =
        Range.closedOpen(new DateTime().withHourOfDay(8).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0),
                         new DateTime().withHourOfDay(8).withMinuteOfHour(0).withSecondOfMinute(1).withMillisOfSecond(0));
    assertEquals(formatter.formatDateTime(testRange), "08:00");
  }

  @Test
  public void testZeroRangeDuration()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.TIME_AND_DURATION);
    final Range<DateTime> testRange =
        Range.closedOpen(testDateTime1.withHourOfDay(8).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0),
                         testDateTime1.withHourOfDay(8).withMinuteOfHour(0).withSecondOfMinute(1).withMillisOfSecond(0));
    assertEquals(formatter.formatDateTime(testRange), "08:00");
  }

  @Test
  public void testRangeDateCurrentMonth()
  {
    final DateTime lower = DateTime.now().withDayOfMonth(5).withHourOfDay(9).withMinuteOfHour(0);
    final DateTime upper = lower.plusDays(3);
    final Range<DateTime> currentYearRange = Range.closedOpen(lower, upper);
    // Need short version of day and month
    final String startDay = lower.toString(shortDayFmt);
    final String endDay = upper.minusDays(1).toString(shortDayFmt);
    final String month = lower.toString(shortMonthFmt);
    final String expected = startDay + " 5 - " + endDay + " 7 " + month;
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatDate(currentYearRange), expected);
  }

  @Test
  public void testRangeDateSameDay()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final Range<DateTime> testRange = Range.closedOpen(new DateTime(thisYear, 7, 3, 0, 0), new DateTime(thisYear, 7, 4, 0, 0));
    final String day = testRange.lowerEndpoint().toString(shortDayFmt);
    assertEquals(formatter.formatDate(testRange), day + " 3 Jul");
  }

  @Test
  public void testRangeTodayDayDuration()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.TIME_AND_DURATION);
    final Range<DateTime> testRange =
        Range.closedOpen(new DateTime().withHourOfDay(8).withMinuteOfHour(0), new DateTime().withHourOfDay(10).withMinuteOfHour(0));
    assertEquals(formatter.formatDateTime(testRange), "08:00\n2hr");
  }

  @Test
  public void testRangeTodayDayDurationWithMinutes()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.TIME_AND_DURATION);
    final Range<DateTime> testRange =
        Range.closedOpen(new DateTime().withHourOfDay(8).withMinuteOfHour(0), new DateTime().withHourOfDay(10).withMinuteOfHour(15));
    assertEquals(formatter.formatDateTime(testRange), "08:00\n2hr15m");
  }

  @Test
  public void testRangeTodayDayDurationWithoutHours()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.TIME_AND_DURATION);
    final Range<DateTime> testRange =
        Range.closedOpen(new DateTime().withHourOfDay(8).withMinuteOfHour(0), new DateTime().withHourOfDay(8).withMinuteOfHour(45));
    assertEquals(formatter.formatDateTime(testRange), "08:00\n45m");
  }

  @Test
  public void testRangeDateSpanningDays()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final String startDay = testDateTimeRange2.lowerEndpoint().toString(shortDayFmt);
    final String endDay = testDateTimeRange2.upperEndpoint().minusDays(1).toString(shortDayFmt);
    assertEquals(formatter.formatDate(testDateTimeRange2), startDay + " 2 - " + endDay + " 3 Apr " + Long.toString(thisYear - 1));
  }

  @Test
  public void testRangeSpanningDays()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.TIME_AND_DURATION);
    final String startDay = testDateTimeRange2.lowerEndpoint().toString(shortDayFmt);
    final String endDay = testDateTimeRange2.upperEndpoint().minusDays(1).toString(shortDayFmt);
    assertEquals(formatter.formatDateTime(testDateTimeRange2), "06:00\n57hr");
  }

  @Test
  public void testRangeDateSpanningMonths()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final String startDay = testDateTimeRange4.lowerEndpoint().toString(shortDayFmt);
    final String endDay = testDateTimeRange4.upperEndpoint().minusDays(1).toString(shortDayFmt);
    assertEquals(formatter.formatDate(testDateTimeRange4), startDay + " 30 Apr - " + endDay + " 31 May " + Long.toString(thisYear - 1));
  }

  @Test
  public void testRangeDateSpanningYears()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final String startDay = testDateTimeRange5.lowerEndpoint().toString(shortDayFmt);
    final String endDay = testDateTimeRange5.upperEndpoint().minusDays(1).toString(shortDayFmt);
    assertEquals(formatter.formatDate(testDateTimeRange5), startDay + " 30 Apr " + Long.toString(thisYear - 2) + " - " + endDay + " 31 May " + Long.toString(thisYear - 1));
  }

  @Test
  public void testRangeDateTime()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final String startDay = testDateTimeRange1.lowerEndpoint().toString(shortDayFmt);
    assertEquals(formatter.formatDateTime(testDateTimeRange1), startDay + " 1 Mar 00:00 - 01:00");
  }

  @Test
  public void testRangeTime()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.TIME_ONLY);
    assertEquals(formatter.formatDateTime(testDateTimeRange1), "00:00 - 01:00");
  }

  @Test
  public void testRangeDateTimeCurrentDay()
  {
    final DateTime lower = DateTime.now().withHourOfDay(9).withMinuteOfHour(0);
    final DateTime upper = lower.plusHours(4);
    final Range<DateTime> currentYearRange = Range.closedOpen(lower, upper);
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatDateTime(currentYearRange), "09:00 - 13:00");
  }

  @Test
  public void testRangeDateTimeSpanningDays()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final String startDay = testDateTimeRange2.lowerEndpoint().toString(shortDayFmt);
    final String endDay = testDateTimeRange2.upperEndpoint().toString(shortDayFmt);
    assertEquals(formatter.formatDateTime(testDateTimeRange2), startDay + " 2 Apr " + Long.toString(thisYear - 1) + " 06:00 - " + endDay + " 4 Apr " + Long.toString(thisYear - 1) + " 15:00");
  }

  @Test
  public void testRangeDateTimeSpanningMonths()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final String startDay = testDateTimeRange4.lowerEndpoint().toString(shortDayFmt);
    final String endDay = testDateTimeRange4.upperEndpoint().toString(shortDayFmt);
    assertEquals(formatter.formatDateTime(testDateTimeRange4), startDay + " 30 Apr " + Long.toString(thisYear - 1) + " 12:00 - " + endDay + " 1 Jun " + Long.toString(thisYear - 1) + " 09:00");
  }

  @Test
  public void testRangeDateTimeSpanningYears()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final String startDay = testDateTimeRange5.lowerEndpoint().toString(shortDayFmt);
    final String endDay = testDateTimeRange5.upperEndpoint().toString(shortDayFmt);
    assertEquals(formatter.formatDateTime(testDateTimeRange5), startDay + " 30 Apr " + Long.toString(thisYear - 2) + " 12:00 - " + endDay + " 1 Jun " + Long.toString(thisYear - 1) + " 09:00");
  }

  @Test
  public void testDateWithTimeOnlyStyle()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.TIME_ONLY);
    assertEquals(formatter.formatDate(testDateTimeRange2), null);
  }

  // Ensure that a range in next year but on the same day shows correctly
  @Test
  public void testRangeDateTimeNextYearSameDay()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final DateTime lower = DateTime.now().withHourOfDay(9).withMinuteOfHour(0).withDayOfMonth(2).withMonthOfYear(6).plusYears(1);
    final DateTime upper = lower.plusHours(1);
    final Range<DateTime> nextYearRange = Range.closedOpen(lower, upper);

    // Need short version of day and month
    final String day = lower.toString(shortDayFmt);
    final String month = lower.toString(shortMonthFmt);
    final String year = lower.toString(yearFmt);
    final String expected = day + " 2 " + month + " " + year + " " + lower.toString(timeFmt) + " - " + upper.toString(timeFmt);
    assertEquals(formatter.formatDateTime(nextYearRange), expected);
  }

  @Test
  public void testRangeDateTimeNoLowerBound()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.TIME_ONLY);
    final DateTime upper = DateTime.now().withHourOfDay(9).withMinuteOfHour(0).withDayOfMonth(14).withMonthOfYear(10).plusYears(1);
    final Range<DateTime> range = Range.upTo(upper, BoundType.OPEN);

    assertEquals(formatter.formatDateTime(range), "... - 09:00");
  }

  @Test
  public void testRangeDateTimeNoUpperBound()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.TIME_ONLY);
    final DateTime lower = DateTime.now().withHourOfDay(9).withMinuteOfHour(0).withDayOfMonth(14).withMonthOfYear(10).plusYears(1);
    final Range<DateTime> range = Range.downTo(lower, BoundType.CLOSED);

    assertEquals(formatter.formatDateTime(range), "09:00 - ...");
  }

  @Test
  public void testRangeDateTimeNoBound()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final Range<DateTime> range = Range.all();

    assertEquals(formatter.formatDateTime(range), "...");
  }

  @Test
  public void testRangeDateNoLowerBound()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.NORMAL);
    final DateTime upper = DateTime.now().withHourOfDay(9).withMinuteOfHour(0).withDayOfMonth(14).withMonthOfYear(10).plusYears(1);
    final Range<DateTime> range = Range.upTo(upper, BoundType.OPEN);

    final String day = upper.minusDays(1).toString(shortDayFmt);
    final String month = upper.minusDays(1).toString(shortMonthFmt);
    final String year = upper.minusDays(1).toString(yearFmt);
    final String expected = "... - " + day + " 13 " + month + " " + year;

    assertEquals(formatter.formatDate(range), expected);
  }

  @Test
  public void testRangeDateNoUpperBound()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.NORMAL);
    final DateTime lower = DateTime.now().withHourOfDay(9).withMinuteOfHour(0).withDayOfMonth(14).withMonthOfYear(10).plusYears(1);
    final Range<DateTime> range = Range.downTo(lower, BoundType.CLOSED);

    final String day = lower.toString(shortDayFmt);
    final String month = lower.toString(shortMonthFmt);
    final String year = lower.toString(yearFmt);
    final String expected = day + " 14 " + month + " " + year + " - ...";
    assertEquals(formatter.formatDate(range), expected);
  }

  @Test
  public void testRangeDateNoBound()
  {
    final RangeFormatter formatter = new RangeFormatter();
    final Range<DateTime> range = Range.all();

    assertEquals(formatter.formatDate(range), "...");
  }
 }

