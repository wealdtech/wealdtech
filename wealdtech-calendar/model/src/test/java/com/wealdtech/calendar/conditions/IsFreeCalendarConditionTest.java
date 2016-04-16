/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.calendar.conditions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.wealdtech.calendar.Calendar;
import com.wealdtech.calendar.Event;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.wealdtech.calendar.conditions.CalendarConditions.isFree;
import static org.testng.Assert.*;

/**
 *
 */
public class IsFreeCalendarConditionTest
{
  private Calendar testCalendar1;

  private DateTime DAY_1 = new DateTime(2016, 5, 1, 0, 0, DateTimeZone.forID("Europe/London"));

  @BeforeClass
  public void setUp()
  {

    // Set up our test calendar
    final ImmutableList<Event> events = ImmutableList.of(Event.builder()
                                                              .summary("Test event 1")
                                                              .startDateTime(DAY_1.withHourOfDay(9))
                                                              .endDateTime(DAY_1.withHourOfDay(10))
                                                              .build());
    testCalendar1 = Calendar.builder().summary("Test calendar 1").events(events).build();
  }

  @Test
  public void testIsFree1()
  {
    final Range<DateTime> testRange = Range.closedOpen(DAY_1.withHourOfDay(6), DAY_1.withHourOfDay(7));
    assertTrue(isFree(testCalendar1, testRange));
  }

  @Test
  public void testIsFree2()
  {
    final Range<DateTime> testRange = Range.closedOpen(DAY_1.withHourOfDay(9), DAY_1.withHourOfDay(10));
    assertFalse(isFree(testCalendar1, testRange));
  }

  @Test
  public void testIsFree3()
  {
    final Range<DateTime> testRange = Range.closedOpen(DAY_1.withHourOfDay(8), DAY_1.withHourOfDay(9));
    assertTrue(isFree(testCalendar1, testRange));
  }

  @Test
  public void testIsFree4()
  {
    final Range<DateTime> testRange = Range.closedOpen(DAY_1.withHourOfDay(10), DAY_1.withHourOfDay(11));
    assertTrue(isFree(testCalendar1, testRange));
  }

  @Test
  public void testIsFree5()
  {
    // This is an empty range so expect it to be free
    final Range<DateTime> testRange = Range.closedOpen(DAY_1.withHourOfDay(9), DAY_1.withHourOfDay(9));
    assertTrue(isFree(testCalendar1, testRange));
  }
}
