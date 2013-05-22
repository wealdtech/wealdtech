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
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.wealdtech.schedule.Occurrence;
import com.wealdtech.schedule.Schedule;
import com.wealdtech.utils.Accessor;

import static org.testng.Assert.*;

public class ScheduleAccessorTest
{
  @Test
  public void testSimple() throws Exception
  {
    // Simple daily schedule
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 1, 1, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .daysOfYear(Schedule.ALL)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  1, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  2, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  3, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  4, 9, 0));
    for (int i = 0; i < 1000; i++)
    {
      accessor.next();
    }
    assertEquals(accessor.next().getStart(), new DateTime(2015, 10, 2, 9, 0));
  }

  @Test
  public void testSpecificDaysOfYear() throws Exception
  {
    // Restrict to specific days of the year
    final ImmutableList<Integer> daysOfYear = ImmutableList.of(1, 60, 300);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 1, 1, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .daysOfYear(daysOfYear)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  1, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013,  3,  1, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013, 10, 27, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  1,  1, 9, 0));
    for (int i = 0; i < 1000; i++)
    {
      assertTrue(daysOfYear.contains(accessor.next().getStart().getDayOfYear()));
    }
    // FIXME check
    assertEquals(accessor.next().getStart(), new DateTime(2347, 10, 27, 9, 0));
  }

  @Test
  public void testSpecificDaysOfMonth() throws Exception
  {
    // Restrict to specific days of the month
    final ImmutableList<Integer> daysOfMonth = ImmutableList.of(1, 2, 3, 4, 5);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 1, 3, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .monthsOfYear(Schedule.ALL)
                                          .daysOfMonth(daysOfMonth)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  3, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  4, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  5, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013,  2,  1, 9, 0));
    for (int i = 0; i < 1000; i++)
    {
      assertTrue(daysOfMonth.contains(accessor.next().getStart().getDayOfMonth()));
    }
    // FIXME check
    assertEquals(accessor.next().getStart(), new DateTime(2029,  10,  2, 9, 0));
  }

  @Test
  public void testSpecificDaysOfWeek() throws Exception
  {
    // Restrict to specific days of the week
    final ImmutableList<Integer> daysOfWeek = ImmutableList.of(1, 2, 3, 6);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 1, 1, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .weeksOfYear(Schedule.ALL)
                                          .daysOfWeek(daysOfWeek)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  1, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  2, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  5, 9, 0));
    for (int i = 0; i < 1000; i++)
    {
      assertTrue(daysOfWeek.contains(accessor.next().getStart().getDayOfWeek()));
    }
    // FIXME check
    assertEquals(accessor.next().getStart(), new DateTime(2017,  10, 23, 9, 0));
  }

  @Test
  public void testSpecificMonthsOfYear() throws Exception
  {
    // Restrict to specific months of the year
    final ImmutableList<Integer> monthsOfYear = ImmutableList.of(5, 3, 1);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 1, 1, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .monthsOfYear(monthsOfYear)
                                          .daysOfMonth(Schedule.ALL)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  1, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  2, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  3, 9, 0));
    for (int i = 0; i < 1000; i++)
    {
      assertTrue(monthsOfYear.contains(accessor.next().getStart().getMonthOfYear()), "Month of year at iteration " + i + " incorrect");
    }
    // FIXME check
    assertEquals(accessor.next().getStart(), new DateTime(2023,  5, 12, 9, 0));
  }

  @Test
  public void testLastWeekOfYear() throws Exception
  {
    // Ensure that the last week of the year is rolled over correctly
    final ImmutableList<Integer> daysOfWeek = ImmutableList.of(1, 2, 3, 5, 6);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 12, 30, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .weeksOfYear(Schedule.ALL)
                                          .daysOfWeek(daysOfWeek)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2013, 12, 30, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013, 12, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  1,  1, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  1,  3, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  1,  4, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  1,  6, 9, 0));
  }

  @Test
  public void testLastDayOfYear() throws Exception
  {
    // Ensure that the last day of the year is rolled over correctly
    final ImmutableList<Integer> daysOfYear = ImmutableList.of(1, 60, 365);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013,  1,  1, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .daysOfYear(daysOfYear)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1,  1, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013,  3,  1, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2013, 12, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  1,  1, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  3,  1, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014, 12, 31, 9, 0));
  }

  @Test
  public void test366DayOfYear() throws Exception
  {
    // Ensure that a schedule with 366th day of year works
    final ImmutableList<Integer> daysOfYear = ImmutableList.of(366);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2016, 12, 31, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .daysOfYear(daysOfYear)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2016, 12, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2020, 12, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2024, 12, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2028, 12, 31, 9, 0));
  }

  @Test
  public void test29thFeb() throws Exception
  {
    // Ensure that a schedule for 29th February works
    final ImmutableList<Integer> daysOfMonth = ImmutableList.of(29);
    final ImmutableList<Integer> monthsOfYear = ImmutableList.of(2);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2016,  2, 29, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .daysOfMonth(daysOfMonth)
                                          .monthsOfYear(monthsOfYear)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2016,  2, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2020,  2, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2024,  2, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2028,  2, 29, 9, 0));
  }

  @Test
  public void test28and29thFeb() throws Exception
  {
    // Ensure that a schedule for 28th and 29th February works
    final ImmutableList<Integer> daysOfMonth = ImmutableList.of(28, 29);
    final ImmutableList<Integer> monthsOfYear = ImmutableList.of(2);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2016,  2, 28, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .daysOfMonth(daysOfMonth)
                                          .monthsOfYear(monthsOfYear)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2016,  2, 28, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2016,  2, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2017,  2, 28, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2018,  2, 28, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2019,  2, 28, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2020,  2, 28, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2020,  2, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2021,  2, 28, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2022,  2, 28, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2023,  2, 28, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2024,  2, 28, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2024,  2, 29, 9, 0));
  }

  @Test
  public void test53WeekOfYear() throws Exception
  {
    // Ensure that a schedule with 2nd day of 53rd week of year works
    final ImmutableList<Integer> daysOfWeek = ImmutableList.of(2);
    final ImmutableList<Integer> weeksOfYear = ImmutableList.of(53);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2024, 12, 31, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .weeksOfYear(weeksOfYear)
                                          .daysOfWeek(daysOfWeek)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2024, 12, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2030, 12, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2036, 12, 30, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2041, 12, 31, 9, 0));
  }

  @Test
  public void test31OfMonth() throws Exception
  {
    // Ensure that a schedule for the 31st of the month works
    final ImmutableList<Integer> daysOfMonth = ImmutableList.of(31);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2014,  1, 31, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .daysOfMonth(daysOfMonth)
                                          .monthsOfYear(Schedule.ALL)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2014,  1, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  3, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  5, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  7, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  8, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014, 10, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014, 12, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2015,  1, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2015,  3, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2015,  5, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2015,  7, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2015,  8, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2015, 10, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2015, 12, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2016,  1, 31, 9, 0));
  }

  @Test
  public void testLastDaysOfMonth() throws Exception
  {
    // Ensure that a schedule which hits the last days of each month works
    final ImmutableList<Integer> daysOfMonth = ImmutableList.of(28, 29, 30, 31);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2014,  1, 28, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .daysOfMonth(daysOfMonth)
                                          .monthsOfYear(Schedule.ALL)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2014,  1, 28, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  1, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  1, 30, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  1, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  2, 28, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  3, 28, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  3, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  3, 30, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  3, 31, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  4, 28, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  4, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  4, 30, 9, 0));
  }

  @Test
  public void testDoMandMoYCombo() throws Exception
  {
    // Ensure that a schedule which contains both day of month and month of
    // year works
    final ImmutableList<Integer> daysOfMonth = ImmutableList.of(29);
    final ImmutableList<Integer> monthsOfYear = ImmutableList.of(1, 2);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013,  1, 29, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .daysOfMonth(daysOfMonth)
                                          .monthsOfYear(monthsOfYear)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    assertEquals(accessor.next().getStart(), new DateTime(2013,  1, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2014,  1, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2015,  1, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2016,  1, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2016,  2, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2017,  1, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2018,  1, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2019,  1, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2020,  1, 29, 9, 0));
    assertEquals(accessor.next().getStart(), new DateTime(2020,  2, 29, 9, 0));
  }

//  @Test
//  public void testDaysOfMonthAndMonthsOfYear() throws Exception
//  {
//    // Combined days of month and months of year schedule
//    final ImmutableList<Integer> daysOfMonth = ImmutableList.of(1, 2, 3, 4, 5);
//    final ImmutableList<Integer> monthsOfYear = ImmutableList.of(5, 3, 1);
//    final Schedule schedule = new Schedule.Builder()
//                                          .start(new DateTime(2013, 3, 3, 9, 0))
//                                          .duration(new Period(Hours.ONE))
//                                          .monthsOfYear(monthsOfYear)
//                                          .daysOfMonth(daysOfMonth)
//                                          .build();
//    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
//    Occurrence oc1 = accessor.next();
//    assertEquals(oc1.getStart(), new DateTime(2013, 3, 3, 9, 0));
//    Occurrence oc2 = accessor.next();
//    assertEquals(oc2.getStart(), new DateTime(2013, 3, 4, 9, 0));
//    Occurrence oc3 = accessor.next();
//    assertEquals(oc3.getStart(), new DateTime(2013, 3, 5, 9, 0));
//    for (int i = 0; i < 1000; i++)
//    {
//      Occurrence ocl = accessor.next();
//      assertTrue(daysOfMonth.contains(ocl.getStart().getDayOfMonth()));
//      assertTrue(monthsOfYear.contains(ocl.getStart().getMonthOfYear()));
//    }
//    Occurrence oc4 = accessor.next();
//    assertEquals(oc4.getStart(), new DateTime(2080, 3, 1, 9, 0));
//  }
//
////  @Test
//  public void testDaysOfWeek() throws Exception
//  {
//    // Simple days of week schedule
//    final ImmutableList<Integer> daysOfWeek = ImmutableList.of(2, 3, 5, 6);
//    final Schedule schedule = new Schedule.Builder()
//                                          .start(new DateTime(2013, 1, 1, 9, 0))
//                                          .duration(new Period(Hours.ONE))
//                                          .weeksOfYear(Schedule.ALL)
//                                          .daysOfWeek(daysOfWeek)
//                                          .build();
//    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
//    Occurrence oc1 = accessor.next();
//    assertEquals(oc1.getStart(), new DateTime(2013, 1, 1, 9, 0));
//    Occurrence oc2 = accessor.next();
//    assertEquals(oc2.getStart(), new DateTime(2013, 1, 2, 9, 0));
//    Occurrence oc3 = accessor.next();
//    assertEquals(oc3.getStart(), new DateTime(2013, 1, 4, 9, 0));
//    for (int i = 0; i < 1000; i++)
//    {
//      Occurrence ocl = accessor.next();
//      System.err.println(ocl.getStart());
//      assertTrue(daysOfWeek.contains(ocl.getStart().getDayOfWeek()), "Date " + ocl.getStart() + " invalid at iteration " + i);
//    }
//    Occurrence oc4 = accessor.next();
//    // FIXME check
//    assertEquals(oc4.getStart(), new DateTime(2017, 10, 27, 9, 0));
//  }
//
//  @Test
//  public void testWeeksOfYear() throws Exception
//  {
//    // Simple weeks of year schedule
//    final Schedule schedule = new Schedule.Builder()
//                                          .start(new DateTime(2013, 1, 1, 9, 0))
//                                          .duration(new Period(Hours.ONE))
//                                          .weeksOfYear(Schedule.ALL)
//                                          .daysOfWeek(2)
//                                          .build();
//    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
//    Occurrence oc1 = accessor.next();
//    assertEquals(oc1.getStart(), new DateTime(2013, 1, 1, 9, 0));
//    Occurrence oc2 = accessor.next();
//    assertEquals(oc2.getStart(), new DateTime(2013, 1, 8, 9, 0));
//    Occurrence oc3 = accessor.next();
//    assertEquals(oc3.getStart(), new DateTime(2013, 1, 15, 9, 0));
//    for (int i = 0; i < 1000; i++)
//    {
//      Occurrence ocl = accessor.next();
//      assertTrue(ocl.getStart().getDayOfWeek() == 2, "Date " + ocl.getStart() + " invalid at iteration " + i + ", getRelativeWeekOfYear() returned " + Schedule.getRelativeWeekOfYear(ocl.getStart()));
//    }
//    Occurrence oc4 = accessor.next();
//    // FIXME check
//    assertEquals(oc4.getStart(), new DateTime(2032, 7, 6, 9, 0));
//  }
//
//  @Test
//  public void testWeeksOfYear2() throws Exception
//  {
//    // Simple weeks of year schedule
//    final Schedule schedule = new Schedule.Builder()
//                                          .start(new DateTime(2013, 1, 1, 9, 0))
//                                          .duration(new Period(Hours.ONE))
//                                          .weeksOfYear(1)
//                                          .daysOfWeek(2)
//                                          .build();
//    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
//    Occurrence oc1 = accessor.next();
//    assertEquals(oc1.getStart(), new DateTime(2013, 1, 1, 9, 0));
//    Occurrence oc2 = accessor.next();
//    assertEquals(oc2.getStart(), new DateTime(2014, 1, 7, 9, 0));
//    Occurrence oc3 = accessor.next();
//    assertEquals(oc3.getStart(), new DateTime(2015, 1, 6, 9, 0));
//    for (int i = 0; i < 1000; i++)
//    {
//      Occurrence ocl = accessor.next();
//      assertTrue(Schedule.getRelativeWeekOfYear(ocl.getStart()) == 1, "Date " + ocl.getStart() + " invalid at iteration " + i + ", getRelativeWeekOfYear() returned " + Schedule.getRelativeWeekOfYear(ocl.getStart()));
//    }
//    Occurrence oc4 = accessor.next();
//    // FIXME check
//    assertEquals(oc4.getStart(), new DateTime(3016, 1, 2, 9, 0));
//  }
//
//  @Test
//  public void testWeeksOfYear3() throws Exception
//  {
//    // Weeks of year schedule with multiple entries
//    final ImmutableList<Integer> weeksOfYear = ImmutableList.of(1, 52);
//    final Schedule schedule = new Schedule.Builder()
//                                          .start(new DateTime(2013, 1, 1, 9, 0))
//                                          .duration(new Period(Hours.ONE))
//                                          .weeksOfYear(weeksOfYear)
//                                          .daysOfWeek(2)
//                                          .build();
//    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
//    Occurrence oc1 = accessor.next();
//    assertEquals(oc1.getStart(), new DateTime(2013, 1, 1, 9, 0));
//    Occurrence oc2 = accessor.next();
//    assertEquals(oc2.getStart(), new DateTime(2013, 12, 24, 9, 0));
//    Occurrence oc3 = accessor.next();
//    assertEquals(oc3.getStart(), new DateTime(2014, 1, 7, 9, 0));
//    for (int i = 0; i < 1000; i++)
//    {
//      Occurrence ocl = accessor.next();
//      assertTrue(weeksOfYear.contains(Schedule.getRelativeWeekOfYear(ocl.getStart())), "Date " + ocl.getStart() + " invalid at iteration " + i + ", getRelativeWeekOfYear() returned " + Schedule.getRelativeWeekOfYear(ocl.getStart()));
//    }
//    Occurrence oc4 = accessor.next();
//    // FIXME check
//    assertEquals(oc4.getStart(), new DateTime(2514, 12, 25, 9, 0));
//  }
//
//  @Test
//  public void testWeeksOfYear4() throws Exception
//  {
//    // Weeks of year schedule with multiple entries and (often) out-of-range values
//    final ImmutableList<Integer> weeksOfYear = ImmutableList.of(1, 52, 54);
//    final Schedule schedule = new Schedule.Builder()
//                                          .start(new DateTime(2013, 1, 1, 9, 0))
//                                          .duration(new Period(Hours.ONE))
//                                          .weeksOfYear(weeksOfYear)
//                                          .daysOfWeek(2)
//                                          .build();
//    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
//    Occurrence oc1 = accessor.next();
//    assertEquals(oc1.getStart(), new DateTime(2013, 1, 1, 9, 0));
//    Occurrence oc2 = accessor.next();
//    assertEquals(oc2.getStart(), new DateTime(2013, 12, 24, 9, 0));
//    Occurrence oc3 = accessor.next();
//    assertEquals(oc3.getStart(), new DateTime(2014, 1, 7, 9, 0));
//    for (int i = 0; i < 1000; i++)
//    {
//      Occurrence ocl = accessor.next();
//      assertTrue(weeksOfYear.contains(Schedule.getRelativeWeekOfYear(ocl.getStart())), "Date " + ocl.getStart() + " invalid at iteration " + i + ", getRelativeWeekOfYear() returned " + Schedule.getRelativeWeekOfYear(ocl.getStart()));
//    }
//    Occurrence oc4 = accessor.next();
//    // FIXME check
//    assertEquals(oc4.getStart(), new DateTime(2514, 12, 25, 9, 0));
//  }

}
