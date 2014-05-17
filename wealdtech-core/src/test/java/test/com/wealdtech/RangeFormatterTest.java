/*
 * Copyright 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech;

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
  private static DateTime testDateTime1 = new DateTime(2014, 3, 1, 0, 0, DateTimeZone.UTC);
  private static DateTime testDateTime2 = new DateTime(2014, 3, 1, 1, 0, DateTimeZone.UTC);
  private static Range<DateTime> testDateTimeRange1 = Range.closedOpen(testDateTime1, testDateTime2);

  private static DateTime testDateTime3 = new DateTime(2013, 4, 2, 6, 0, DateTimeZone.UTC);
  private static DateTime testDateTime4 = new DateTime(2013, 4, 4, 15, 0, DateTimeZone.UTC);
  private static Range<DateTime> testDateTimeRange2 = Range.closedOpen(testDateTime3, testDateTime4);

  private static DateTime testDateTime5 = new DateTime(2013, 3, 2, 8, 0, DateTimeZone.forID("Asia/Tokyo"));
  private static DateTime testDateTime6 = new DateTime(2013, 3, 3, 9, 0, DateTimeZone.forID("Asia/Tokyo"));
  private static Range<DateTime> testDateTimeRange3 = Range.closedOpen(testDateTime5, testDateTime6);

  private static DateTime testDateTime7 = new DateTime(2013, 4, 30, 12, 0, DateTimeZone.forID("Asia/Tokyo"));
  private static DateTime testDateTime8 = new DateTime(2013, 6, 1, 9, 0, DateTimeZone.forID("Asia/Tokyo"));
  private static Range<DateTime> testDateTimeRange4 = Range.closedOpen(testDateTime7, testDateTime8);

  private static DateTime testDateTime9 = new DateTime(2011, 4, 30, 12, 0, DateTimeZone.forID("Asia/Tokyo"));
  private static DateTime testDateTime10 = new DateTime(2013, 6, 1, 9, 0, DateTimeZone.forID("Asia/Tokyo"));
  private static Range<DateTime> testDateTimeRange5 = Range.closedOpen(testDateTime9, testDateTime10);

  private static final DateTimeFormatter shortDayFmt = new DateTimeFormatterBuilder().appendDayOfWeekShortText().toFormatter();
  private static final DateTimeFormatter shortMonthFmt = new DateTimeFormatterBuilder().appendMonthOfYearShortText().toFormatter();
  @Test
  public void testSingleDate()
  {
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatDate(testDateTime1), "Sat 1 Mar");
  }

  @Test
  public void testSingleDateLongForm()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.FULL);
    assertEquals(formatter.formatDate(testDateTime1), "Saturday 1 March");
  }

  @Test
  public void testSingleDateDifferentYear()
  {
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatDate(testDateTime5), "Sat 2 Mar 2013");
  }

  @Test
  public void testSingleDateDifferentYearLongForm()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.FULL);
    assertEquals(formatter.formatDate(testDateTime5), "Saturday 2 March 2013");
  }

  @Test
  public void testSingleDateTime()
  {
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatDateTime(testDateTime1), "Sat 1 Mar 00:00");
  }

  @Test
  public void testSingleDateNonDefaultLocale()
  {
    final RangeFormatter formatter = new RangeFormatter(Locale.FRANCE, RangeFormatter.Style.NORMAL);
    assertEquals(formatter.formatDateTime(testDateTime1), "sam. 1 mars 00:00");
  }

  @Test
  public void testSingleDateTimeLongForm()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.FULL);
    assertEquals(formatter.formatDateTime(testDateTime1), "Saturday 1 March 00:00");
  }

  @Test
  public void testSingleDateTimeDifferentYear()
  {
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatDateTime(testDateTime5), "Sat 2 Mar 2013 08:00");
  }

  @Test
  public void testSingleDateTimeDifferentYearLongForm()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.FULL);
    assertEquals(formatter.formatDateTime(testDateTime5), "Saturday 2 March 2013 08:00");
  }

  @Test
  public void testSingleTime()
  {
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatTime(testDateTime3), "06:00");
  }

  @Test
  public void testSingleTimeNonDefaultLocale()
  {
    final RangeFormatter formatter = new RangeFormatter(Locale.CANADA, RangeFormatter.Style.NORMAL);
    assertEquals(formatter.formatTime(testDateTime3), "06:00");
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
    final Range<DateTime> testRange = Range.closedOpen(new DateTime(2014, 7, 3, 0, 0), new DateTime(2014, 7, 4, 0, 0));
    assertEquals(formatter.formatDate(testRange), "Thu 3 Jul");
  }


  @Test
  public void testRangeDateSpanningDays()
  {
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatDate(testDateTimeRange2), "Tue 2 - Wed 3 Apr 2013");
  }

  @Test
  public void testRangeDateSpanningMonths()
  {
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatDate(testDateTimeRange4), "Tue 30 Apr - Fri 31 May 2013");
  }

  @Test
  public void testRangeDateSpanningYears()
  {
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatDate(testDateTimeRange5), "Sat 30 Apr 2011 - Fri 31 May 2013");
  }

  @Test
  public void testRangeDateTime()
  {
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatDateTime(testDateTimeRange1), "Sat 1 Mar 00:00 - 01:00");
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
    assertEquals(formatter.formatDateTime(testDateTimeRange2), "Tue 2 Apr 2013 06:00 - Thu 4 Apr 2013 15:00");
  }

  @Test
  public void testRangeDateTimeSpanningMonths()
  {
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatDateTime(testDateTimeRange4), "Tue 30 Apr 2013 12:00 - Sat 1 Jun 2013 09:00");
  }

  @Test
  public void testRangeDateTimeSpanningYears()
  {
    final RangeFormatter formatter = new RangeFormatter();
    assertEquals(formatter.formatDateTime(testDateTimeRange5), "Sat 30 Apr 2011 12:00 - Sat 1 Jun 2013 09:00");
  }

  @Test
  public void testDateWithTimeOnlyStyle()
  {
    final RangeFormatter formatter = new RangeFormatter(RangeFormatter.Style.TIME_ONLY);
    assertEquals(formatter.formatDate(testDateTimeRange2), null);
  }
}

