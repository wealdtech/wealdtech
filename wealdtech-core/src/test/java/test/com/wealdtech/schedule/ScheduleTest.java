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

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Period;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.wealdtech.DataError;
import com.wealdtech.schedule.Schedule;

import static org.testng.Assert.*;

public class ScheduleTest
{
  @BeforeClass
  public void setUp()
  {
  }

  @Test
  public void testModel() throws Exception
  {
    final Schedule rs1 = new Schedule.Builder()
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

    final Schedule rs2 = new Schedule.Builder(rs1)
                                     .start(new DateTime(2012, 2, 5, 1, 0))
                                     .duration(new Period(Hours.ONE))
                                     .monthsOfYear(2)
                                     .daysOfMonth(5)
                                     .build();
    assertNotNull(rs2);
    rs2.toString();
    rs2.hashCode();
    assertNotEquals(null, rs2);
    assertEquals(rs2, rs2);
    assertNotEquals(rs1, rs2);
  }

  @Test
  public void testInvalidNoStartTime() throws Exception
  {
    try
    {
      new Schedule.Builder()
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
      new Schedule.Builder()
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
  public void testInvalidBadYear() throws Exception
  {
    try
    {
      new Schedule.Builder()
                  .start(new DateTime(2012, 1, 5, 1, 0))
                  .duration(new Period(Hours.ONE))
                  .yearGap(-1)
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
      new Schedule.Builder()
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
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 1), new DateTime(2012, 1, 1, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 2), new DateTime(2012, 1, 2, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 3), new DateTime(2012, 1, 3, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 4), new DateTime(2012, 1, 4, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 5), new DateTime(2012, 1, 5, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 6), new DateTime(2012, 1, 6, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt, 7), new DateTime(2012, 1, 7, 1, 0));


    final DateTime dt2 = new DateTime(2012, 1, 12, 1, 0);
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 1), new DateTime(2012, 1, 8, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 2), new DateTime(2012, 1, 9, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 3), new DateTime(2012, 1, 10, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 4), new DateTime(2012, 1, 11, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 5), new DateTime(2012, 1, 12, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 6), new DateTime(2012, 1, 13, 1, 0));
    assertEquals(Schedule.withDayOfAbsoluteWeek(dt2, 7), new DateTime(2012, 1, 14, 1, 0));
  }

  @Test
  public void testWithAbsoluteWeekOfYear() throws Exception
  {
    final DateTime dt = new DateTime(2012, 1, 1, 1, 0); // Monday
    assertEquals(Schedule.withAbsoluteWeekOfYear(dt, 1), new DateTime(2012, 1, 1, 1, 0));
    assertEquals(Schedule.withAbsoluteWeekOfYear(dt, 2), new DateTime(2012, 1, 8, 1, 0));
    assertEquals(Schedule.withAbsoluteWeekOfYear(dt, 3), new DateTime(2012, 1, 15, 1, 0));
    assertEquals(Schedule.withAbsoluteWeekOfYear(dt, 4), new DateTime(2012, 1, 22, 1, 0));
    assertEquals(Schedule.withAbsoluteWeekOfYear(dt, 5), new DateTime(2012, 1, 29, 1, 0));
    assertEquals(Schedule.withAbsoluteWeekOfYear(dt, 6), new DateTime(2012, 2, 5, 1, 0));

    final DateTime dt2 = new DateTime(2012, 6, 15, 1, 0); // Friday
    assertEquals(Schedule.withAbsoluteWeekOfYear(dt2, 1), new DateTime(2012, 1, 6, 1, 0));
    assertEquals(Schedule.withAbsoluteWeekOfYear(dt2, 30), new DateTime(2012, 7, 27, 1, 0));
    assertEquals(Schedule.withAbsoluteWeekOfYear(dt2, 35), new DateTime(2012, 8, 31, 1, 0));
    assertEquals(Schedule.withAbsoluteWeekOfYear(dt2, 52), new DateTime(2012, 12, 28, 1, 0));
  }
}
