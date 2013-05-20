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
  public void testDaysOfYear() throws Exception
  {
    // Simple daily schedule
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 1, 1, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .daysOfYear(Schedule.ALL)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    Occurrence oc1 = accessor.next();
    assertEquals(oc1.getStart(), new DateTime(2013, 1, 1, 9, 0));
    Occurrence oc2 = accessor.next();
    assertEquals(oc2.getStart(), new DateTime(2013, 1, 2, 9, 0));
    Occurrence oc3 = accessor.next();
    assertEquals(oc3.getStart(), new DateTime(2013, 1, 3, 9, 0));
    for (int i = 0; i < 1000; i++)
    {
      accessor.next();
    }
    Occurrence oc4 = accessor.next();
    assertEquals(oc4.getStart(), new DateTime(2015, 10, 1, 9, 0));
  }

  @Test
  public void testDaysOfMonth() throws Exception
  {
    // Simple days of month schedule
    final ImmutableList<Integer> daysOfMonth = ImmutableList.of(1, 2, 3, 4, 5);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 1, 3, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .monthsOfYear(Schedule.ALL)
                                          .daysOfMonth(daysOfMonth)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    Occurrence oc1 = accessor.next();
    assertEquals(oc1.getStart(), new DateTime(2013, 1, 3, 9, 0));
    Occurrence oc2 = accessor.next();
    assertEquals(oc2.getStart(), new DateTime(2013, 1, 4, 9, 0));
    Occurrence oc3 = accessor.next();
    assertEquals(oc3.getStart(), new DateTime(2013, 1, 5, 9, 0));
    for (int i = 0; i < 1000; i++)
    {
      Occurrence ocl = accessor.next();
      assertTrue(daysOfMonth.contains(ocl.getStart().getDayOfMonth()));
    }
    Occurrence oc4 = accessor.next();
    // FIXME check
    assertEquals(oc4.getStart(), new DateTime(2029, 10, 1, 9, 0));
  }

  @Test
  public void testMonthsOfYear() throws Exception
  {
    // Simple months of year schedule
    final ImmutableList<Integer> monthsOfYear = ImmutableList.of(5, 3, 1);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 1, 1, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .monthsOfYear(monthsOfYear)
                                          .daysOfMonth(Schedule.ALL)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    Occurrence oc1 = accessor.next();
    assertEquals(oc1.getStart(), new DateTime(2013, 1, 1, 9, 0));
    Occurrence oc2 = accessor.next();
    assertEquals(oc2.getStart(), new DateTime(2013, 1, 2, 9, 0));
    Occurrence oc3 = accessor.next();
    assertEquals(oc3.getStart(), new DateTime(2013, 1, 3, 9, 0));
    for (int i = 0; i < 30; i++)
    {
      Occurrence ocl = accessor.next();
      assertTrue(monthsOfYear.contains(ocl.getStart().getMonthOfYear()));
    }
    Occurrence oc4 = accessor.next();
    assertEquals(oc4.getStart(), new DateTime(2013, 3, 3, 9, 0));
    for (int i = 0; i < 1000; i++)
    {
      Occurrence ocl = accessor.next();
      assertTrue(monthsOfYear.contains(ocl.getStart().getMonthOfYear()));
    }
    Occurrence oc5 = accessor.next();
    // FIXME check
    assertEquals(oc5.getStart(), new DateTime(2024, 1, 12, 9, 0));
  }


  @Test
  public void testDaysOfMonthAndMonthsOfYear() throws Exception
  {
    // Combined days of month and months of year schedule
    final ImmutableList<Integer> daysOfMonth = ImmutableList.of(1, 2, 3, 4, 5);
    final ImmutableList<Integer> monthsOfYear = ImmutableList.of(5, 3, 1);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 3, 3, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .monthsOfYear(monthsOfYear)
                                          .daysOfMonth(daysOfMonth)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    Occurrence oc1 = accessor.next();
    assertEquals(oc1.getStart(), new DateTime(2013, 3, 3, 9, 0));
    Occurrence oc2 = accessor.next();
    assertEquals(oc2.getStart(), new DateTime(2013, 3, 4, 9, 0));
    Occurrence oc3 = accessor.next();
    assertEquals(oc3.getStart(), new DateTime(2013, 3, 5, 9, 0));
    for (int i = 0; i < 30; i++)
    {
      Occurrence ocl = accessor.next();
      assertTrue(daysOfMonth.contains(ocl.getStart().getDayOfMonth()));
      assertTrue(monthsOfYear.contains(ocl.getStart().getMonthOfYear()));
    }
    Occurrence oc4 = accessor.next();
    // FIXME check
    assertEquals(oc4.getStart(), new DateTime(2015, 5, 1, 9, 0));
    for (int i = 0; i < 1000; i++)
    {
      Occurrence ocl = accessor.next();
      assertTrue(daysOfMonth.contains(ocl.getStart().getDayOfMonth()));
      assertTrue(monthsOfYear.contains(ocl.getStart().getMonthOfYear()));
    }
    Occurrence oc5 = accessor.next();
    // FIXME check
    assertEquals(oc5.getStart(), new DateTime(2082, 3, 2, 9, 0));
  }

  @Test
  public void testDaysOfWeek() throws Exception
  {
    // Simple days of week schedule
    final ImmutableList<Integer> daysOfWeek = ImmutableList.of(2, 3, 5, 6);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 1, 1, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .weeksOfYear(Schedule.ALL)
                                          .daysOfWeek(daysOfWeek)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    Occurrence oc1 = accessor.next();
    assertEquals(oc1.getStart(), new DateTime(2013, 1, 1, 9, 0));
    Occurrence oc2 = accessor.next();
    assertEquals(oc2.getStart(), new DateTime(2013, 1, 2, 9, 0));
    Occurrence oc3 = accessor.next();
    assertEquals(oc3.getStart(), new DateTime(2013, 1, 4, 9, 0));
    for (int i = 0; i < 1000; i++)
    {
      Occurrence ocl = accessor.next();
      assertTrue(daysOfWeek.contains(ocl.getStart().getDayOfWeek()), "Date " + ocl.getStart() + " invalid at iteration " + i);
    }
    Occurrence oc4 = accessor.next();
    // FIXME check
    assertEquals(oc4.getStart(), new DateTime(2017, 11, 11, 9, 0));
  }

  @Test
  public void testWeeksOfYear() throws Exception
  {
    // Simple weeks of year schedule
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 1, 1, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .weeksOfYear(Schedule.ALL)
                                          .daysOfWeek(2)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    Occurrence oc1 = accessor.next();
    assertEquals(oc1.getStart(), new DateTime(2013, 1, 1, 9, 0));
    Occurrence oc2 = accessor.next();
    assertEquals(oc2.getStart(), new DateTime(2013, 1, 8, 9, 0));
    Occurrence oc3 = accessor.next();
    assertEquals(oc3.getStart(), new DateTime(2013, 1, 15, 9, 0));
    for (int i = 0; i < 1000; i++)
    {
      Occurrence ocl = accessor.next();
      assertTrue(ocl.getStart().getDayOfWeek() == 2, "Date " + ocl.getStart() + " invalid at iteration " + i + ", getRelativeWeekOfYear() returned " + Schedule.getRelativeWeekOfYear(ocl.getStart()));
    }
    Occurrence oc4 = accessor.next();
    // FIXME check
    assertEquals(oc4.getStart(), new DateTime(2032, 7, 6, 9, 0));
  }

  @Test
  public void testWeeksOfYear2() throws Exception
  {
    // Simple weeks of year schedule
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 1, 1, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .weeksOfYear(1)
                                          .daysOfWeek(2)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    Occurrence oc1 = accessor.next();
    assertEquals(oc1.getStart(), new DateTime(2013, 1, 1, 9, 0));
    Occurrence oc2 = accessor.next();
    assertEquals(oc2.getStart(), new DateTime(2014, 1, 7, 9, 0));
    Occurrence oc3 = accessor.next();
    assertEquals(oc3.getStart(), new DateTime(2015, 1, 6, 9, 0));
    for (int i = 0; i < 1000; i++)
    {
      Occurrence ocl = accessor.next();
      assertTrue(Schedule.getRelativeWeekOfYear(ocl.getStart()) == 1, "Date " + ocl.getStart() + " invalid at iteration " + i + ", getRelativeWeekOfYear() returned " + Schedule.getRelativeWeekOfYear(ocl.getStart()));
    }
    Occurrence oc4 = accessor.next();
    // FIXME check
    assertEquals(oc4.getStart(), new DateTime(3016, 1, 2, 9, 0));
  }

  @Test
  public void testWeeksOfYear3() throws Exception
  {
    // Weeks of year schedule with multiple entries
    final ImmutableList<Integer> weeksOfYear = ImmutableList.of(1, 52);
    final Schedule schedule = new Schedule.Builder()
                                          .start(new DateTime(2013, 1, 1, 9, 0))
                                          .duration(new Period(Hours.ONE))
                                          .weeksOfYear(weeksOfYear)
                                          .daysOfWeek(2)
                                          .build();
    final Accessor<Occurrence, DateTime> accessor = schedule.accessor();
    Occurrence oc1 = accessor.next();
    assertEquals(oc1.getStart(), new DateTime(2013, 1, 1, 9, 0));
    Occurrence oc2 = accessor.next();
    assertEquals(oc2.getStart(), new DateTime(2013, 12, 24, 9, 0));
    Occurrence oc3 = accessor.next();
    assertEquals(oc3.getStart(), new DateTime(2014, 1, 7, 9, 0));
    for (int i = 0; i < 1000; i++)
    {
      Occurrence ocl = accessor.next();
      assertTrue(weeksOfYear.contains(Schedule.getRelativeWeekOfYear(ocl.getStart())), "Date " + ocl.getStart() + " invalid at iteration " + i + ", getRelativeWeekOfYear() returned " + Schedule.getRelativeWeekOfYear(ocl.getStart()));
    }
    Occurrence oc4 = accessor.next();
    // FIXME check
    assertEquals(oc4.getStart(), new DateTime(2514, 12, 25, 9, 0));
  }
}
