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

package com.wealdtech.schedule;

import org.joda.time.DateTime;

import com.google.common.collect.ImmutableList;
import com.wealdtech.utils.Accessor;

/**
 * A ScheduleAccessor allows access to occurrences within a schedule
 */
public class ScheduleAccessor implements Accessor<Occurrence, DateTime>
{
  private final transient Schedule schedule;

  private transient DateTime mark;
  private transient boolean preset = false;

  private transient Integer curMonthsOfYearIndex;
  private transient Integer curWeeksOfYearIndex;
  private transient Integer curWeeksOfMonthIndex;
  private transient Integer curDaysOfYearIndex;
  private transient Integer curDaysOfMonthIndex;
  private transient Integer curDaysOfWeekIndex;

  public ScheduleAccessor(final Schedule schedule)
  {
    this(schedule, schedule.getStart());
  }

  public ScheduleAccessor(final Schedule schedule, final DateTime mark)
  {
    this.schedule = schedule;
    this.mark = mark;
    resetIndices();
  }

  // Reset the indices tracking our schedule according to current mark
  private void resetIndices()
  {
    // TODO relate to current mark rather than resetting to 0
    if (this.schedule.getMonthsOfYear().isPresent())
    {
      this.curMonthsOfYearIndex = 0;
    }
    if (this.schedule.getWeeksOfYear().isPresent())
    {
      this.curWeeksOfYearIndex = 0;
    }
    if (this.schedule.getWeeksOfMonth().isPresent())
    {
      this.curWeeksOfMonthIndex = 0;
    }
    if (this.schedule.getDaysOfYear().isPresent())
    {
      this.curDaysOfYearIndex = 0;
    }
    if (this.schedule.getDaysOfMonth().isPresent())
    {
      this.curDaysOfMonthIndex = 0;
    }
    if (this.schedule.getDaysOfWeek().isPresent())
    {
      this.curDaysOfWeekIndex = 0;
    }
    preset = true;
  }

  @Override
  public void setBaseItem(Occurrence mark)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void setBase(DateTime mark)
  {
    this.mark = mark;
  }

  @Override
  public boolean hasNext()
  {
    return !this.schedule.terminates();
  }

  @Override
  public Occurrence next()
  {
    if (this.preset == true)
    {
      // Use the preset value
      this.preset = false;
    }
    else
    {
      // We work on the basis of increments and rollovers.
      boolean rolledover = false;
      rolledover = nextDay();
      if (rolledover)
      {
        // Our days rolled over, go to weeks
        rolledover = nextWeek();
        if (rolledover)
        {
          rolledover = nextMonth();
          if (rolledover)
          {
            nextYear();
          }
        }
      }
    }

    return new Occurrence(this.mark, this.mark.plus(this.schedule.getDuration()));
  }

  // Get the next year for our schedule
  private void nextYear()
  {
    this.mark = this.mark.plusYears(this.schedule.getYearGap() + 1);
  }

  // Get the next month for our schedule
  private boolean nextMonth()
  {
    boolean rollingover = true;
    if (this.schedule.getMonthsOfYear().isPresent())
    {
      rollingover = nextMonthOfYear();
    }
    return rollingover;
  }

  // Get the next month for our months-per-year schedule
  private boolean nextMonthOfYear()
  {
    boolean rollingover = false;
    ImmutableList<Integer> monthsOfYear = this.schedule.getMonthsOfYear().get();
    if (monthsOfYear.contains(Schedule.ALL))
    {
      // Every month
      if (this.mark.equals(this.mark.monthOfYear().withMaximumValue()))
      {
        this.mark = this.mark.monthOfYear().withMinimumValue();
        rollingover = true;
      }
      else
      {
        this.mark = this.mark.plusMonths(1);
      }
    }
    else
    {
      // Specific months
      if (this.curMonthsOfYearIndex < monthsOfYear.size() - 1)
      {
        int months = monthsOfYear.get(this.curMonthsOfYearIndex + 1) - monthsOfYear.get(this.curMonthsOfYearIndex);
        this.curMonthsOfYearIndex++;
        this.mark = this.mark.plusMonths(months);
      }
      else
      {
        int months = monthsOfYear.get(this.curMonthsOfYearIndex) - monthsOfYear.get(0);
        this.curMonthsOfYearIndex = 0;
        this.mark = this.mark.minusMonths(months);
        rollingover = true;
      }
    }
    return rollingover;
  }

  // Get the next week for our schedule
  private boolean nextWeek()
  {
    boolean rollingover = true;
    if (this.schedule.getWeeksOfMonth().isPresent())
    {
      rollingover = nextWeekOfMonth();
    }
    else if (this.schedule.getWeeksOfYear().isPresent())
    {
      rollingover = nextWeekOfYear();
    }
    return rollingover;
  }

  // Get the next week for our weeks-per-month schedule
  private boolean nextWeekOfMonth()
  {
    boolean rollingover = false;
    ImmutableList<Integer> weeksOfMonth = this.schedule.getWeeksOfMonth().get();
    if (weeksOfMonth.contains(Schedule.ALL))
    {
      // Every week
      // TODO check to see if 7 days ahead is in the next month
      this.mark = this.mark.plusWeeks(1);
    }
    else
    {
      // Specific weeks
      if (this.curWeeksOfMonthIndex < weeksOfMonth.size() - 1)
      {
        int weeks = weeksOfMonth.get(this.curWeeksOfMonthIndex + 1) - weeksOfMonth.get(this.curWeeksOfMonthIndex);
        this.curWeeksOfMonthIndex++;
        this.mark = this.mark.plusWeeks(weeks);
      }
      else
      {
        int weeks = weeksOfMonth.get(this.curWeeksOfMonthIndex) - weeksOfMonth.get(0);
        this.curWeeksOfMonthIndex = 0;
        this.mark = this.mark.minusWeeks(weeks);
        rollingover = true;
      }
    }
    return rollingover;
  }

  // Get the next week for our weeks-per-year schedule
  private boolean nextWeekOfYear()
  {
    boolean rollingover = false;
    ImmutableList<Integer> weeksOfYear = this.schedule.getWeeksOfYear().get();
    if (weeksOfYear.contains(Schedule.ALL))
    {
      // Every week
      if (this.mark.equals(this.mark.weekOfWeekyear().withMaximumValue()))
      {
        this.mark = this.mark.weekOfWeekyear().withMinimumValue();
        rollingover = true;
      }
      else
      {
        this.mark = this.mark.plusWeeks(1);
      }
    }
    else
    {
      // Specific weeks
      if (this.curWeeksOfYearIndex < weeksOfYear.size() - 1)
      {
        int weeks = weeksOfYear.get(this.curWeeksOfYearIndex + 1) - weeksOfYear.get(this.curWeeksOfYearIndex);
        this.curWeeksOfYearIndex++;
        this.mark = this.mark.plusWeeks(weeks);
      }
      else
      {
        int weeks = weeksOfYear.get(this.curWeeksOfYearIndex) - weeksOfYear.get(0);
        this.curWeeksOfYearIndex = 0;
        this.mark = this.mark.minusWeeks(weeks);
        rollingover = true;
      }
    }
    return rollingover;
  }

  // Get the next day for our schedule
  private boolean nextDay()
  {
    boolean rollingover = true;
    if (this.schedule.getDaysOfWeek().isPresent())
    {
      rollingover = nextDayOfWeek();
    }
    else if (this.schedule.getDaysOfMonth().isPresent())
    {
      rollingover = nextDayOfMonth();
    }
    else if (this.schedule.getDaysOfYear().isPresent())
    {
      rollingover = nextDayOfYear();
    }
    return rollingover;
  }

  // Get the next day for our days-per-week schedule
  private boolean nextDayOfWeek()
  {
    boolean rollingover = false;
    ImmutableList<Integer> daysOfWeek = this.schedule.getDaysOfWeek().get();
    if (daysOfWeek.contains(Schedule.ALL))
    {
      // Every day
      if (this.mark.equals(this.mark.dayOfWeek().withMaximumValue()))
      {
        this.mark = this.mark.dayOfWeek().withMinimumValue();
        rollingover = true;
      }
      else
      {
        this.mark = this.mark.plusDays(1);
      }
    }
    else
    {
      // Specific days
      if (this.curDaysOfWeekIndex < daysOfWeek.size() - 1)
      {
        int days = daysOfWeek.get(this.curDaysOfWeekIndex + 1) - daysOfWeek.get(this.curDaysOfWeekIndex);
        this.curDaysOfWeekIndex++;
        this.mark = this.mark.plusDays(days);
      }
      else
      {
        int days = daysOfWeek.get(this.curDaysOfWeekIndex) - daysOfWeek.get(0);
        this.curDaysOfWeekIndex = 0;
        this.mark = this.mark.minusDays(days);
        rollingover = true;
      }
    }
    return rollingover;
  }

  // Get the next day for our days-per-month schedule
  private boolean nextDayOfMonth()
  {
    boolean rollingover = false;
    ImmutableList<Integer> daysOfMonth = this.schedule.getDaysOfMonth().get();
    if (daysOfMonth.contains(Schedule.ALL))
    {
      // Every day
      if (this.mark.equals(this.mark.dayOfMonth().withMaximumValue()))
      {
        this.mark = this.mark.dayOfMonth().withMinimumValue();
        rollingover = true;
      }
      else
      {
        this.mark = this.mark.plusDays(1);
      }
    }
    else
    {
      if (this.curDaysOfMonthIndex < daysOfMonth.size() - 1)
      {
        int days = daysOfMonth.get(this.curDaysOfMonthIndex + 1) - daysOfMonth.get(this.curDaysOfMonthIndex);
        this.curDaysOfMonthIndex++;
        this.mark = this.mark.plusDays(days);
      }
      else
      {
        int days = daysOfMonth.get(this.curDaysOfMonthIndex) - daysOfMonth.get(0);
        this.curDaysOfMonthIndex = 0;
        this.mark = this.mark.minusDays(days);
        rollingover = true;
      }
    }
    return rollingover;
  }

  // Get the next day for our days-per-year schedule
  private boolean nextDayOfYear()
  {
    boolean rollingover = false;
    ImmutableList<Integer> daysOfYear = this.schedule.getDaysOfYear().get();
    if (daysOfYear.contains(Schedule.ALL))
    {
      // Every day
      if (this.mark.equals(this.mark.dayOfYear().withMaximumValue()))
      {
        this.mark = this.mark.dayOfYear().withMinimumValue();
        rollingover  = true;
      }
      else
      {
        this.mark = this.mark.plusDays(1);
      }
    }
    else
    {
      // Specific days
      if (this.curDaysOfYearIndex < daysOfYear.size() - 1)
      {
        int days = daysOfYear.get(this.curDaysOfYearIndex + 1) - daysOfYear.get(this.curDaysOfYearIndex);
        this.curDaysOfYearIndex++;
        this.mark = this.mark.plusDays(days);
      }
      else
      {
        int days = daysOfYear.get(this.curDaysOfYearIndex) - daysOfYear.get(0);
        this.curDaysOfYearIndex = 0;
        this.mark = this.mark.minusDays(days);
        rollingover = true;
      }
    }
    return rollingover;
  }

  @Override
  public boolean hasPrevious()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Occurrence previous()
  {
    // TODO Auto-generated method stub
    return null;
  }
}
