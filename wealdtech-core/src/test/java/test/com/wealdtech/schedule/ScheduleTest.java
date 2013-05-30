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

package test.com.wealdtech.schedule;

import static com.wealdtech.utils.Joda.*;
import static org.testng.Assert.*;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Period;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.wealdtech.DataError;
import com.wealdtech.schedule.Schedule;

public class ScheduleTest
{
  @BeforeClass
  public void setUp()
  {
  }

  @Test
  public void testModel() throws Exception
  {
    final Schedule<DateTime> rs1 = new Schedule.Builder<DateTime>()
                                               .start(new DateTime(2012, 1, 5, 1, 0))
                                               .duration(new Period(Hours.ONE))
                                               .monthsOfYear(1)
                                               .daysOfMonth(5)
                                               .build();
    assertNotNull(rs1);
    rs1.toString();
    rs1.hashCode();
    assertNotEquals(null, rs1);
    assertEquals(rs1, rs1);

    final Schedule<DateTime> rs2 = new Schedule.Builder<DateTime>(rs1)
                                               .start(new DateTime(2012, 2, 5, 1, 0))
                                               .duration(new Period(Hours.ONE))
                                               .monthsOfYear(2, 3)
                                               .daysOfMonth(5, 6)
                                               .build();
    assertNotNull(rs2);
    rs2.toString();
    rs2.hashCode();
    assertNotEquals(null, rs2);
    assertEquals(rs2, rs2);
    assertNotEquals(rs1, rs2);

    final Schedule<DateTime> rs3 = new Schedule.Builder<DateTime>()
                                               .start(new DateTime(2012, 3, 5, 1, 0))
                                               .duration(new Period(Hours.ONE))
                                               .daysOfWeek(1, 2, 3)
                                               .weeksOfMonth(1, 2)
                                               .monthsOfYear(3, 5, 7)
                                               .build();
    assertNotNull(rs3);
    rs3.toString();
    rs3.hashCode();
    assertNotEquals(null, rs3);
    assertEquals(rs3, rs3);
    assertNotEquals(rs1, rs3);

    final Schedule<DateTime> rs4 = new Schedule.Builder<DateTime>()
                                               .start(new DateTime(2012, 3, 5, 1, 0))
                                               .duration(new Period(Hours.ONE))
                                               .daysOfWeek(Schedule.ALL)
                                               .weeksOfMonth(Schedule.ALL)
                                               .monthsOfYear(Schedule.ALL)
                                               .build();
    assertNotNull(rs4);
    rs4.toString();
    rs4.hashCode();
    assertNotEquals(null, rs4);
    assertEquals(rs4, rs4);
    assertNotEquals(rs1, rs4);

    final Schedule<DateTime> rs5 = new Schedule.Builder<DateTime>()
                                               .start(new DateTime(2012, 3, 5, 1, 0))
                                               .duration(new Period(Hours.ONE))
                                               .daysOfMonth(Schedule.ALL)
                                               .monthsOfYear(Schedule.ALL)
                                               .build();
    assertNotNull(rs5);
    rs5.toString();
    rs5.hashCode();
    assertNotEquals(null, rs5);
    assertEquals(rs5, rs5);
    assertNotEquals(rs1, rs5);

    final Schedule<DateTime> rs6 = new Schedule.Builder<DateTime>()
                                               .start(new DateTime(2012, 3, 5, 1, 0))
                                               .duration(new Period(Hours.ONE))
                                               .daysOfYear(Schedule.ALL)
                                               .build();
    assertNotNull(rs6);
    rs6.toString();
    rs6.hashCode();
    assertNotEquals(null, rs6);
    assertEquals(rs6, rs6);
    assertNotEquals(rs1, rs6);
}

  @Test
  public void testAlternateBuilder() throws Exception
  {
    new Schedule.Builder<DateTime>()
                .start(new DateTime(2012, 1, 5, 1, 0))
                .duration(new Period(Hours.ONE))
                .weeksOfMonth(ImmutableList.<Integer>of())
                .weeksOfYear(ImmutableList.<Integer>of())
                .monthsOfYear(1)
                .daysOfWeek(ImmutableList.<Integer>of())
                .daysOfMonth(5)
                .daysOfYear(ImmutableList.<Integer>of())
                .build();

    new Schedule.Builder<DateTime>()
                .start(new DateTime(2012, 1, 1, 1, 0))
                .duration(new Period(Hours.ONE))
                .weeksOfMonth(ImmutableList.<Integer>of())
                .weeksOfYear(ImmutableList.<Integer>of())
                .monthsOfYear(ImmutableList.<Integer>of())
                .daysOfWeek(ImmutableList.<Integer>of())
                .daysOfMonth(ImmutableList.<Integer>of())
                .daysOfYear(1)
                .build();
  }
  @Test
  public void testInvalidNoStartTime() throws Exception
  {
    try
    {
      new Schedule.Builder<DateTime>()
                  .duration(new Period(Hours.ONE))
                  .monthsOfYear(1)
                  .daysOfMonth(5)
                  .build();
      fail("Created schedule without start date");
    }
    catch (DataError.Missing de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidMonthAndWeekOfYear() throws Exception
  {
    try
    {
      new Schedule.Builder<DateTime>()
                  .start(new DateTime(2012, 1, 5, 1, 0))
                  .duration(new Period(Hours.ONE))
                  .monthsOfYear(1)
                  .weeksOfYear(5)
                  .build();
      fail("Created schedule with both month and week of year");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidIncompleteNoWeeksOf() throws Exception
  {
    try
    {
      new Schedule.Builder<DateTime>()
                  .start(new DateTime(2012, 1, 5, 1, 0))
                  .duration(new Period(Hours.ONE))
                  .daysOfWeek(1)
                  .build();
      fail("Created schedule with no weeksOf specifier");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidIncompleteNoMonthsOfYear1() throws Exception
  {
    try
    {
      new Schedule.Builder<DateTime>()
                  .start(new DateTime(2012, 1, 5, 1, 0))
                  .duration(new Period(Hours.ONE))
                  .daysOfWeek(1)
                  .weeksOfMonth(1)
                  .build();
      fail("Created schedule with no monthsOfYear specifier");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidIncompleteNoMonthsOfYear2() throws Exception
  {
    try
    {
      new Schedule.Builder<DateTime>()
                  .start(new DateTime(2012, 1, 5, 1, 0))
                  .duration(new Period(Hours.ONE))
                  .daysOfMonth(1)
                  .build();
      fail("Created schedule with no monthsOfYear specifier");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidMultipleDayOf1() throws Exception
  {
    try
    {
      new Schedule.Builder<DateTime>()
                  .start(new DateTime(2012, 1, 5, 1, 0))
                  .duration(new Period(Hours.ONE))
                  .daysOfWeek(5)
                  .daysOfMonth(1)
                  .weeksOfYear(5)
                  .build();
      fail("Created schedule with multiple daysOf entries");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidMultipleDayOf2() throws Exception
  {
    try
    {
      new Schedule.Builder<DateTime>()
                  .start(new DateTime(2012, 1, 5, 1, 0))
                  .duration(new Period(Hours.ONE))
                  .daysOfWeek(5)
                  .daysOfMonth(1)
                  .monthsOfYear(5)
                  .build();
      fail("Created schedule with multiple daysOf entries");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidMultipleDayOf3() throws Exception
  {
    try
    {
      new Schedule.Builder<DateTime>()
                  .start(new DateTime(2012, 1, 5, 1, 0))
                  .duration(new Period(Hours.ONE))
                  .daysOfWeek(5)
                  .daysOfYear(1)
                  .weeksOfYear(1)
                  .build();
      fail("Created schedule with multiple daysOf entries");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidMultipleDayOf4() throws Exception
  {
    try
    {
      new Schedule.Builder<DateTime>()
                  .start(new DateTime(2012, 1, 5, 1, 0))
                  .duration(new Period(Hours.ONE))
                  .daysOfWeek(5)
                  .daysOfYear(1)
                  .weeksOfMonth(2)
                  .monthsOfYear(1)
                  .build();
      fail("Created schedule with multiple daysOf entries");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidMultipleDayOf5() throws Exception
  {
    try
    {
      new Schedule.Builder<DateTime>()
                  .start(new DateTime(2012, 1, 5, 1, 0))
                  .duration(new Period(Hours.ONE))
                  .daysOfMonth(5)
                  .daysOfYear(1)
                  .monthsOfYear(1)
                  .build();
      fail("Created schedule with multiple daysOf entries");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidMultipleWeeksOf1() throws Exception
  {
    try
    {
      new Schedule.Builder<DateTime>()
                  .start(new DateTime(2012, 1, 5, 1, 0))
                  .duration(new Period(Hours.ONE))
                  .daysOfWeek(5)
                  .weeksOfMonth(2)
                  .weeksOfYear(1)
                  .build();
      fail("Created schedule with multiple weeksOf entries");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidBadYear() throws Exception
  {
    try
    {
      new Schedule.Builder<DateTime>()
                  .start(new DateTime(2012, 1, 5, 1, 0))
                  .duration(new Period(Hours.ONE))
                  .weeksOfYear(5)
                  .daysOfWeek(1)
                  .build();
      fail("Created schedule with invalid year gap");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidBadStart() throws Exception
  {
    try
    {
      new Schedule.Builder<DateTime>()
                  .start(new DateTime(2012, 1, 3, 1, 0))
                  .duration(new Period(Hours.ONE))
                  .weeksOfYear(Schedule.ALL)
                  .daysOfWeek(1)
                  .build();
      fail("Created schedule with invalid start");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testDayOfAbsoluteWeek() throws Exception
  {
    final DateTime dt = new DateTime(2012, 1, 1, 1, 0);
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 1), new DateTime(2012, 1, 2, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 2), new DateTime(2012, 1, 3, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 3), new DateTime(2012, 1, 4, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 4), new DateTime(2012, 1, 5, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 5), new DateTime(2012, 1, 6, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 6), new DateTime(2012, 1, 7, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 7), new DateTime(2012, 1, 1, 1, 0));


    final DateTime dt2 = new DateTime(2012, 6, 28, 1, 0);
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 1), new DateTime(2012, 6, 25, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 2), new DateTime(2012, 6, 26, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 3), new DateTime(2012, 6, 27, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 4), new DateTime(2012, 6, 28, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 5), new DateTime(2012, 6, 29, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 6), new DateTime(2012, 6, 30, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 7), new DateTime(2012, 6, 24, 1, 0));
  }

  @Test
  public void testWithAbsoluteWeekOfYear() throws Exception
  {
    final DateTime dt = new DateTime(2012, 1, 1, 1, 0); // Monday
    assertEquals(dt.withField(AbsWeekOfYear,  1), new DateTime(2012, 1, 1, 1, 0));
    assertEquals(dt.withField(AbsWeekOfYear,  2), new DateTime(2012, 1, 8, 1, 0));
    assertEquals(dt.withField(AbsWeekOfYear,  3), new DateTime(2012, 1, 15, 1, 0));
    assertEquals(dt.withField(AbsWeekOfYear,  4), new DateTime(2012, 1, 22, 1, 0));
    assertEquals(dt.withField(AbsWeekOfYear,  5), new DateTime(2012, 1, 29, 1, 0));
    assertEquals(dt.withField(AbsWeekOfYear,  6), new DateTime(2012, 2, 5, 1, 0));

    final DateTime dt2 = new DateTime(2012, 6, 15, 1, 0); // Friday
    assertEquals(dt2.withField(AbsWeekOfYear,  1), new DateTime(2012, 1, 6, 1, 0));
    assertEquals(dt2.withField(AbsWeekOfYear, 30), new DateTime(2012, 7, 27, 1, 0));
    assertEquals(dt2.withField(AbsWeekOfYear, 35), new DateTime(2012, 8, 31, 1, 0));
    assertEquals(dt2.withField(AbsWeekOfYear, 52), new DateTime(2012, 12, 28, 1, 0));
  }

  @Test
  public void testDateMidnight() throws Exception
  {
    final Schedule<DateMidnight> schedule = new Schedule.Builder<DateMidnight>()
                                                        .start(new DateMidnight(2012, 1, 5))
                                                        .duration(new Period(Days.ONE))
                                                        .monthsOfYear(1)
                                                        .daysOfMonth(5)
                                                        .build();
    assertNotNull(schedule);
    schedule.toString();
    schedule.hashCode();
    assertNotEquals(null, schedule);
    assertEquals(schedule, schedule);
    assertNotEquals(schedule, null);
  }

  // Test for datetimetypefield extensions
  @Test
  public void testAbsWeekOfYear() throws Exception
  {
    DateTime dt = new DateTime(2012, 1, 1, 9, 0);
    for (int i = 0; i < 1000; i++)
    {
      assertEquals(dt.get(AbsWeekOfYear), 1, "Date did not return absolute week of year as 1");
      dt = dt.plusYears(1);
    }
  }

  @Test
  public void testAbsWeekOfMonth() throws Exception
  {
    DateTime dt = new DateTime(2012, 1, 1, 9, 0);
    for (int i = 0; i < 1000; i++)
    {
      assertEquals(dt.get(AbsWeekOfMonth), 1, "Date did not return absolute week of month as 1");
      dt = dt.plusMonths(1);
    }
  }
}
