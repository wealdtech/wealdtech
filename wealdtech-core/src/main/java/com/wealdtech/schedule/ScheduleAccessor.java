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
import com.wealdtech.DataError;
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

  /**
   * Reset the indices tracking our schedule according to the current mark.
   * Note that the current mark might not be a valid schedule, in which
   * case we move it forward to the next valid occurrence prior to resetting
   * the indices
   */
  private void resetIndices()
  {
    if (this.schedule.getMonthsOfYear().isPresent() && (!this.schedule.getMonthsOfYear().get().contains(Schedule.ALL)))
    {
      this.curMonthsOfYearIndex = this.schedule.getMonthsOfYear().get().indexOf(this.mark.getMonthOfYear());
    }
    if (this.schedule.getWeeksOfYear().isPresent() && (!this.schedule.getWeeksOfYear().get().contains(Schedule.ALL)))
    {
      this.curWeeksOfYearIndex = this.schedule.getWeeksOfYear().get().indexOf(this.mark.getWeekOfWeekyear());
    }
    if (this.schedule.getWeeksOfMonth().isPresent() && (!this.schedule.getWeeksOfMonth().get().contains(Schedule.ALL)))
    {
      this.curWeeksOfMonthIndex = 0;
    }
    if (this.schedule.getDaysOfYear().isPresent() && (!this.schedule.getDaysOfYear().get().contains(Schedule.ALL)))
    {
      this.curDaysOfYearIndex = this.schedule.getDaysOfYear().get().indexOf(this.mark.getDayOfYear());
    }
    if (this.schedule.getDaysOfMonth().isPresent() && (!this.schedule.getDaysOfMonth().get().contains(Schedule.ALL)))
    {
      this.curDaysOfMonthIndex = this.schedule.getDaysOfMonth().get().indexOf(this.mark.getDayOfMonth());
    }
    if (this.schedule.getDaysOfWeek().isPresent() && (!this.schedule.getDaysOfWeek().get().contains(Schedule.ALL)))
    {
      this.curDaysOfWeekIndex = this.schedule.getDaysOfWeek().get().indexOf(this.mark.getDayOfWeek());
    }
    this.preset = true;
  }

  /**
   * Reset the mark to provide the correct day of the week
   */
  private void resetDayOfWeek()
  {
    if (this.schedule.getDaysOfWeek().isPresent() && (!this.schedule.getDaysOfWeek().get().contains(Schedule.ALL)))
    {
      // We have either a week of month or a week of year setup.
      if (this.schedule.getWeeksOfYear().isPresent() && (!this.schedule.getWeeksOfYear().get().contains(Schedule.ALL)))
      {
        // This is a week of year setup.  Ensure that we are on the correct week of the year as per the schedule
        this.mark = this.mark.withMonthOfYear(1).withDayOfMonth(1).plusWeeks(this.schedule.getWeeksOfYear().get().get(this.curWeeksOfYearIndex) - 1);
      }
      if (this.schedule.getWeeksOfMonth().isPresent() && (!this.schedule.getWeeksOfMonth().get().contains(Schedule.ALL)))
      {
        // This is a week of month setup.  Ensure that we are on the correct week of the month as per the schedule
        this.mark = this.mark.withDayOfMonth(1).plusWeeks(this.schedule.getWeeksOfMonth().get().get(this.curWeeksOfMonthIndex) - 1);
      }

      // We need to reset the day to ensure that it is a valid day of week
      final DateTime tmp = this.mark.withDayOfWeek(this.schedule.getDaysOfWeek().get().get(this.curDaysOfWeekIndex));
      if (tmp.isBefore(this.mark))
      {
        // We went back in time, which isn't a smart move.  Go forward one week to reset
        this.mark = tmp.plusWeeks(1);
      }
      else
      {
        this.mark = tmp;
      }
    }
  }

  @Override
  public void setBaseItem(Occurrence mark)
  {
    setBase(mark.getStart());
  }

  @Override
  public void setBase(DateTime mark)
  {
    if (!this.schedule.isAScheduleStart(mark))
    {
      throw new DataError.Bad("Date is not a valid member of the schedule");
    }
    this.mark = mark;
    resetIndices();
  }

  @Override
  public boolean hasNext()
  {
    return !this.schedule.terminates();
  }

//  @Override
//  public Occurrence next()
//  {
//    if (this.preset == true)
//    {
//      // Use the preset value
//      this.preset = false;
//    }
//    else
//    {
//      // We work on the basis of increments and rollovers.
//      boolean rolledover = false;
//      rolledover = nextDay();
//      if (rolledover)
//      {
//        // Our days rolled over, go to weeks
//        rolledover = nextWeek();
//        if (rolledover)
//        {
//          rolledover = nextMonth();
//          if (rolledover)
//          {
//            nextYear();
//          }
//        }
//      }
//      resetDayOfWeek(); // FIXME only call when needed
//    }
//    return new Occurrence(this.mark, this.mark.plus(this.schedule.getDuration()));
//  }

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
      // We increment the day and then ensure that it continues to fit the additional constraints
      boolean rolledover = false;
      rolledover = nextDay();
      if (rolledover)
      {
        // Ensure that the new mark meets our constraints and fix it if it doesn't
        rolledover = constrainWeek();
        if (rolledover)
        {
          rolledover = constrainMonth();
          if (rolledover)
          {
            constrainYear();
          }
        }
      }
//      resetDayOfWeek(); // FIXME only call when needed
    }
    return new Occurrence(this.mark, this.mark.plus(this.schedule.getDuration()));
  }

  // Constrain the year to valid values
  private void constrainYear()
  {
    this.mark = this.mark.plusYears(this.schedule.getYearGap());
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

  // Ensure that the updated month fits the current constraints, and if not then fix it so that it does
  private boolean constrainMonth()
  {
    boolean rollingover = true;
    if (this.schedule.getMonthsOfYear().isPresent())
    {
      rollingover = constrainMonthOfYear();
    }
    return rollingover;
  }

  private boolean constrainMonthOfYear()
  {
    boolean rollingover = false;
    ImmutableList<Integer> monthsOfYear = this.schedule.getMonthsOfYear().get();
    if (monthsOfYear.contains(Schedule.ALL))
    {
      // Every month
      if (this.mark.getYear() != this.mark.minusMonths(1).getYear())
      {
        // Rolling over
        rollingover = true;
      }
    }
    else
    {
      // Specific months
      if (this.curMonthsOfYearIndex < monthsOfYear.size() - 1)
      {
        int months = monthsOfYear.get(this.curMonthsOfYearIndex + 1) - monthsOfYear.get(this.curMonthsOfYearIndex);
        this.curMonthsOfYearIndex++;
        // Remember we have already rolled over so need to reduce the delta by 1 here
        this.mark = this.mark.plusMonths(months - 1);
      }
      else
      {
        this.curMonthsOfYearIndex = 0;
        // Rolling over; reset the month to the first valid month of the next year as per the schedule
        this.mark = this.mark.plusYears(1).withMonthOfYear(monthsOfYear.get(0));
        rollingover = true;
      }
    }
    return rollingover;
  }

  // Ensure that the updated week fits the current constraints, and if not then fix it so that it does
  private boolean constrainWeek()
  {
    boolean rollingover = true;
    if (this.schedule.getWeeksOfMonth().isPresent())
    {
      rollingover = constrainWeekOfMonth();
    }
    else if (this.schedule.getWeeksOfYear().isPresent())
    {
      rollingover = constrainWeekOfYear();
    }
    return rollingover;
  }

  private boolean constrainWeekOfMonth()
  {
    // FIXME code!
    return false;
  }

  private boolean constrainWeekOfYear()
  {
    boolean rollingover = false;
    ImmutableList<Integer> weeksOfYear = this.schedule.getWeeksOfYear().get();
    if (weeksOfYear.contains(Schedule.ALL))
    {
      // Every week
      if (this.mark.getYear() != this.mark.minusWeeks(1).getYear())
      {
        // Rolling over
        rollingover = true;
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
        // Rolling over; reset the week to the first valid week of this year as per the schedule
        // FIXME is this valid for consecutive years with different numbers of weeks?
        this.mark = this.mark.plusYears(1).minusWeeks(weeks);
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
      if (this.mark.getMonthOfYear() != this.mark.plusWeeks(1).getYear())
      {
        // Rolling over; reset the week to the first week of this month
//        this.mark = this.mark.minusWeeks(Schedule.getRelativeWeekOfMonth(this.mark) - 1));
        rollingover = true;
      }
      else
      {
        this.mark = this.mark.plusWeeks(1);
      }
    }
    if (weeksOfMonth.contains(Schedule.ALL))
    {
      // Every week
      if (this.mark.plusWeeks(1).getMonthOfYear() > this.mark.getMonthOfYear())
      {
        // FIXME looks like rubbish
        this.mark = this.mark.monthOfYear().withMinimumValue();
        rollingover = true;
      }
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
      if (this.mark.getYear() != this.mark.plusWeeks(1).getYear())
      {
        // Rolling over; reset the week to the first week of this year
        this.mark = this.mark.minusWeeks((Schedule.getRelativeWeekOfYear(this.mark) - 1));
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
        // Rolling over; reset the week to the first valid week of this year as per the schedule
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
      this.mark = this.mark.plusDays(1);
      if (this.mark.getDayOfWeek() == 1)
      {
        // We've rolled over in to another week
        rollingover = true;
      }
    }
    else
    {
      // Specific days
      if (this.curDaysOfWeekIndex != (daysOfWeek.size() - 1))
      {
        int days = daysOfWeek.get(this.curDaysOfWeekIndex + 1) - daysOfWeek.get(this.curDaysOfWeekIndex);
        this.curDaysOfWeekIndex++;
        this.mark = this.mark.plusDays(days);
      }
      else
      {
        // Reached the end of the list, go back to the first entry and increment the week
        this.curDaysOfWeekIndex = 0;
        this.mark = this.mark.withDayOfWeek(daysOfWeek.get(this.curDaysOfWeekIndex)).plusWeeks(1);
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
      this.mark = this.mark.plusDays(1);
      if (this.mark.getDayOfMonth() == 1)
      {
        // We've rolled over in to another month
        rollingover = true;
      }
    }
    else
    {
      // Specific days
      if (this.curDaysOfMonthIndex != (daysOfMonth.size() - 1))
      {
        int days = daysOfMonth.get(this.curDaysOfMonthIndex + 1) - daysOfMonth.get(this.curDaysOfMonthIndex);
        this.curDaysOfMonthIndex++;
        this.mark = this.mark.plusDays(days);
      }
      else
      {
        // Reached the end of the list, go back to the first entry and increment the month
        this.curDaysOfMonthIndex = 0;
        this.mark = this.mark.withDayOfMonth(daysOfMonth.get(this.curDaysOfMonthIndex)).plusMonths(1);
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
      this.mark = this.mark.plusDays(1);
      if (this.mark.getDayOfYear() == 1)
      {
        // We've rolled over in to another year
        rollingover = true;
      }
    }
    else
    {
      // Specific days
      if (this.curDaysOfYearIndex != (daysOfYear.size() - 1))
      {
        int days = daysOfYear.get(this.curDaysOfYearIndex + 1) - daysOfYear.get(this.curDaysOfYearIndex);
        this.curDaysOfYearIndex++;
        this.mark = this.mark.plusDays(days);
      }
      else
      {
        // Reached the end of the list, go back to the first entry and increment the year
        this.curDaysOfYearIndex = 0;
        this.mark = this.mark.withDayOfYear(daysOfYear.get(this.curDaysOfYearIndex)).plusYears(1);
        rollingover = true;
      }
    }
    return rollingover;
  }

//  // Get the next day for our days-per-week schedule
//  private boolean nextDayOfWeek()
//  {
//    boolean rollingover = false;
//    ImmutableList<Integer> daysOfWeek = this.schedule.getDaysOfWeek().get();
//    if (daysOfWeek.contains(Schedule.ALL))
//    {
//      // Every day
//      if (this.mark.equals(this.mark.dayOfWeek().withMaximumValue()))
//      {
//        // Rolling over; reset the day to the first day of the week
//        this.mark = this.mark.withDayOfWeek(1);
//        rollingover = true;
//      }
//      else
//      {
//        this.mark = this.mark.plusDays(1);
//      }
//    }
//    else
//    {
//      // Specific days
//      if (this.curDaysOfWeekIndex < daysOfWeek.size() - 1)
//      {
//        int days = daysOfWeek.get(this.curDaysOfWeekIndex + 1) - daysOfWeek.get(this.curDaysOfWeekIndex);
//        this.curDaysOfWeekIndex++;
//        this.mark = this.mark.plusDays(days);
//      }
//      else
//      {
//        int days = daysOfWeek.get(this.curDaysOfWeekIndex) - daysOfWeek.get(0);
//        this.curDaysOfWeekIndex = 0;
//        // Rolling over; reset the day to the first valid day of this week as per the schedule
//        this.mark = this.mark.minusDays(days);
//        rollingover = true;
//      }
//    }
//    return rollingover;
//  }

//  // Get the next day for our days-per-month schedule
//  private boolean nextDayOfMonth()
//  {
//    boolean rollingover = false;
//    ImmutableList<Integer> daysOfMonth = this.schedule.getDaysOfMonth().get();
//    if (daysOfMonth.contains(Schedule.ALL))
//    {
//      // Every day
//      if (this.mark.equals(this.mark.dayOfMonth().withMaximumValue()))
//      {
//        // Rolling over; reset the day to the first day of the month
//        this.mark = this.mark.withDayOfMonth(1);
//        rollingover = true;
//      }
//      else
//      {
//        this.mark = this.mark.plusDays(1);
//      }
//    }
//    else
//    {
//      if (this.curDaysOfMonthIndex < daysOfMonth.size() - 1)
//      {
//        int days = daysOfMonth.get(this.curDaysOfMonthIndex + 1) - daysOfMonth.get(this.curDaysOfMonthIndex);
//        this.curDaysOfMonthIndex++;
//        this.mark = this.mark.plusDays(days);
//      }
//      else
//      {
//        int days = daysOfMonth.get(this.curDaysOfMonthIndex) - daysOfMonth.get(0);
//        this.curDaysOfMonthIndex = 0;
//        // Rolling over; reset the day to the first valid day of this month as per the schedule
//        this.mark = this.mark.minusDays(days);
//        rollingover = true;
//      }
//    }
//    return rollingover;
//  }
//
//  // Get the next day for our days-per-year schedule
//  private boolean nextDayOfYear()
//  {
//    boolean rollingover = false;
//    ImmutableList<Integer> daysOfYear = this.schedule.getDaysOfYear().get();
//    if (daysOfYear.contains(Schedule.ALL))
//    {
//      // Every day
//      if (this.mark.equals(this.mark.dayOfYear().withMaximumValue()))
//      {
//        // Rolling over; reset the day to the first day of the year
//        this.mark = this.mark.withDayOfYear(1);
//        rollingover  = true;
//      }
//      else
//      {
//        this.mark = this.mark.plusDays(1);
//      }
//    }
//    else
//    {
//      // Specific days
//      if (this.curDaysOfYearIndex < daysOfYear.size() - 1)
//      {
//        int days = daysOfYear.get(this.curDaysOfYearIndex + 1) - daysOfYear.get(this.curDaysOfYearIndex);
//        this.curDaysOfYearIndex++;
//        this.mark = this.mark.plusDays(days);
//      }
//      else
//      {
//        int days = daysOfYear.get(this.curDaysOfYearIndex) - daysOfYear.get(0);
//        this.curDaysOfYearIndex = 0;
//        // Rolling over; reset the day to the first valid day of this year as per the schedule
//        this.mark = this.mark.minusDays(days);
//        rollingover = true;
//      }
//    }
//    return rollingover;
//  }

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
