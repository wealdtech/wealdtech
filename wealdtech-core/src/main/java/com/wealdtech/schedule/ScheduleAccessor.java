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
import org.joda.time.DateTimeFieldType;
import org.joda.time.IllegalFieldValueException;

import com.google.common.collect.ImmutableList;
import com.wealdtech.DataError;
import com.wealdtech.ServerError;
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

  // Internal information for when we are finding our next date
  private transient boolean weekRolledOver;
  private transient boolean monthRolledOver;
  private transient boolean yearRolledOver;

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
      this.curWeeksOfYearIndex = this.schedule.getWeeksOfYear().get().indexOf(Schedule.getAbsoluteWeekOfYear(this.mark));
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
   * Generate a valid mark from the index information and the current mark
   */
  private DateTime generateMark(DateTime mark) throws IllegalFieldValueException
  {
    if (this.schedule.getMonthsOfYear().isPresent() && (!this.schedule.getMonthsOfYear().get().contains(Schedule.ALL)))
    {
      mark = mark.withMonthOfYear(this.schedule.getMonthsOfYear().get().get(this.curMonthsOfYearIndex));
    }
    if (this.schedule.getWeeksOfYear().isPresent() && (!this.schedule.getWeeksOfYear().get().contains(Schedule.ALL)))
    {
      // TODO need to handle this
    }
    if (this.schedule.getWeeksOfMonth().isPresent() && (!this.schedule.getWeeksOfMonth().get().contains(Schedule.ALL)))
    {
      // TOD need to  handle this
    }
    if (this.schedule.getDaysOfYear().isPresent() && (!this.schedule.getDaysOfYear().get().contains(Schedule.ALL)))
    {
      mark = mark.withDayOfYear(this.schedule.getDaysOfYear().get().get(this.curDaysOfYearIndex));
    }
    if (this.schedule.getDaysOfMonth().isPresent() && (!this.schedule.getDaysOfMonth().get().contains(Schedule.ALL)))
    {
      mark = mark.withDayOfMonth(this.schedule.getDaysOfMonth().get().get(this.curDaysOfMonthIndex));
    }
    if (this.schedule.getDaysOfWeek().isPresent() && (!this.schedule.getDaysOfWeek().get().contains(Schedule.ALL)))
    {
      // TODO this won't work if the change moved to a different week.  Need to consider this
      mark = mark.withDayOfWeek(this.schedule.getDaysOfWeek().get().get(this.curDaysOfWeekIndex));
    }
    return mark;
  }

  private DateTime resetWeek(final DateTime mark)
  {
    if (this.schedule.getWeeksOfYear().isPresent())
    {
      return resetWeekOfYear(mark);
    }
    else
    {
      throw new ServerError("Mismatch for week component");
    }
  }

  private DateTime resetWeekOfYear(final DateTime mark)
  {
    if (this.schedule.getWeeksOfYear().get().contains(Schedule.ALL))
    {
      return Schedule.withAbsoluteWeekOfYear(mark, 1);
    }
    else
    {
      this.curMonthsOfYearIndex = 0;
      return Schedule.withAbsoluteWeekOfYear(mark, this.schedule.getWeeksOfYear().get().get(0));
    }
  }

  private DateTime resetMonth(final DateTime mark)
  {
    if (this.schedule.getMonthsOfYear().isPresent())
    {
      return resetMonthOfYear(mark);
    }
    else
    {
      throw new ServerError("Mismatch for month of year component");
    }
  }

  private DateTime resetMonthOfYear(final DateTime mark)
  {
    if (this.schedule.getMonthsOfYear().get().contains(Schedule.ALL))
    {
      return mark.withMonthOfYear(1);
    }
    else
    {
      this.curMonthsOfYearIndex = 0;
      return mark.withMonthOfYear(this.schedule.getMonthsOfYear().get().get(0));
    }
  }

  private DateTime resetDay(final DateTime mark)
  {
    if (this.schedule.getDaysOfWeek().isPresent())
    {
      return resetDayOfWeek(mark);
    }
    else if (this.schedule.getDaysOfMonth().isPresent())
    {
      return resetDayOfMonth(mark);
    }
    else if (this.schedule.getDaysOfYear().isPresent())
    {
      return resetDayOfYear(mark);
    }
    else
    {
      throw new ServerError("Schedule without day component");
    }
  }

  /**
   * Reset the mark to provide the correct day of the week
   */
  private DateTime resetDayOfWeek(DateTime mark)
  {
    if (this.schedule.getDaysOfWeek().get().contains(Schedule.ALL))
    {
      mark = Schedule.withDayOfAbsoluteWeek(mark, 1);
    }
    else
    {
      mark = Schedule.withDayOfAbsoluteWeek(mark, this.schedule.getDaysOfWeek().get().get(this.curDaysOfWeekIndex));
    }
    return mark;
  }

  /**
   * Reset the day of the month to the first valid value
   * @throws IllegalFieldValueException if this results in an invalid date
   */
  private DateTime resetDayOfMonth(DateTime mark) throws IllegalFieldValueException
  {
    if (this.schedule.getDaysOfMonth().get().contains(Schedule.ALL))
    {
      mark = mark.withDayOfMonth(1);
    }
    else
    {
      mark = mark.withDayOfMonth(this.schedule.getDaysOfMonth().get().get(this.curDaysOfMonthIndex));
    }
    return mark;
  }

  /**
   * Reset the day of the year to the first valid value
   * @throws IllegalFieldValueException if this results in an invalid date
   */
  private DateTime resetDayOfYear(DateTime mark) throws IllegalFieldValueException
  {
    if (this.schedule.getDaysOfYear().get().contains(Schedule.ALL))
    {
      mark = mark.withDayOfYear(1);
    }
    else
    {
      mark = mark.withDayOfYear(this.schedule.getDaysOfYear().get().get(this.curDaysOfYearIndex));
    }
    return mark;
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
      nextDay();
    }
    return new Occurrence(this.mark, this.mark.plus(this.schedule.getDuration()));
  }

  // Constrain the year to valid values.
  // Note that there are two considerations here.  First, that the year fits
  // the required interval.  Second, that the resultant date fits the required
  // constraints.  For example, a schedule for the 366th day of the year will
  // not meet its constraints every year so we need to find a suitable year.
  private void constrainYear()
  {
    this.mark = this.mark.plusYears(this.schedule.getYearGap());
    while (!this.schedule.isAScheduleStart(this.mark))
    {
      this.mark = this.mark.plusYears(this.schedule.getYearGap() + 1);
      try
      {
        this.mark = generateMark(this.mark);
      }
      catch (IllegalFieldValueException ifve)
      {
        // Our attempt to reset the mark resulted in an illegal date, for
        // example 29th Feb 2013.  That's okay, we'll move on to the next year
      }
    }
  }

  // Ensure that the updated month fits the current constraints, and if not then fix it so that it does
  private void constrainMonth()
  {
    if (this.schedule.getMonthsOfYear().isPresent())
    {
      constrainMonthOfYear();
    }
  }

  // Constrain the month of the year.  At this point the month has already
  // been incremented so we need to ensure that the new date fits the
  // constraints.
  private void constrainMonthOfYear()
  {
    ImmutableList<Integer> monthsOfYear = this.schedule.getMonthsOfYear().get();
    DateTime nextMark = this.mark;
    if (!monthsOfYear.contains(Schedule.ALL))
    {
      try
      {
        if (this.curMonthsOfYearIndex == monthsOfYear.size() - 1)
        {
          // Reached the end of our specified months of the year; reset
          throw new IllegalFieldValueException(DateTimeFieldType.monthOfYear(), null, null);
        }
        nextMark = nextMark.withMonthOfYear(monthsOfYear.get(++this.curMonthsOfYearIndex));
        nextMark = resetDay(nextMark);
      }
      catch (IllegalFieldValueException ifve)
      {
        // Our attempt to increment the month caused an invalid value, which means that
        // there are no more valid values for this year.  Roll the year over and find
        // the next valid value
        this.yearRolledOver = true;
        this.curMonthsOfYearIndex = 0;
        nextMark = this.mark.withMonthOfYear(1);
        while (true)
        {
          nextMark = nextMark.plusYears(1);
          try
          {
            nextMark = nextMark.withMonthOfYear(monthsOfYear.get(0));
            nextMark = resetDay(nextMark);
            break;
          }
          catch (IllegalFieldValueException ifve2)
          {
            // No luck with this year
          }
        }
      }
    }
    this.yearRolledOver = (this.mark.getYear() != nextMark.getYear());
    this.mark = nextMark;
  }

  // Ensure that the updated week fits the current constraints, and if not then fix it so that it does
  private void constrainWeek()
  {
    if (this.schedule.getWeeksOfMonth().isPresent())
    {
      constrainWeekOfMonth();
    }
    else if (this.schedule.getWeeksOfYear().isPresent())
    {
      constrainWeekOfYear();
    }
  }

  private void constrainWeekOfMonth()
  {
    // FIXME code!
    return;
  }

  private void constrainWeekOfYear()
  {
    ImmutableList<Integer> weeksOfYear = this.schedule.getWeeksOfYear().get();
    DateTime nextMark = null;
    if (!weeksOfYear.contains(Schedule.ALL))
    {
      // Specific weeks
      if (this.curWeeksOfYearIndex < weeksOfYear.size() - 1 && !this.yearRolledOver)
      {
        int weeks = weeksOfYear.get(this.curWeeksOfYearIndex + 1) - weeksOfYear.get(this.curWeeksOfYearIndex);
        this.curWeeksOfYearIndex++;
        // We have already rolled over so need to reduce the delta by 1 here
        nextMark = this.mark.plusWeeks(weeks - 1);
      }
      else
      {
        int weeks = weeksOfYear.get(this.curWeeksOfYearIndex) - weeksOfYear.get(0);
        this.curWeeksOfYearIndex = 0;
        // Rolling over; reset the week to the first valid week of this year as per the schedule
        // FIXME is this valid for consecutive years with different numbers of weeks?
        nextMark = this.mark.plusYears(1).minusWeeks(weeks);
      }
    }
    if (nextMark != null)
    {
      this.yearRolledOver = (this.mark.getYear() != nextMark.getYear());
      this.mark = nextMark;
    }
  }

  // Get the next day for our schedule
  private void nextDay()
  {
    if (this.schedule.getDaysOfWeek().isPresent())
    {
      nextDayOfWeek();
    }
    else if (this.schedule.getDaysOfMonth().isPresent())
    {
      nextDayOfMonth();
    }
    else if (this.schedule.getDaysOfYear().isPresent())
    {
      nextDayOfYear();
    }
  }

  // Get the next day for our days-per-week schedule
  // Note that this uses the standard Monday->Sunday definition of a week,
  // as opposed to the week-of-year and week-of-month definitions which use
  // the first day of the year or month to define the start of the first week.
  private void nextDayOfWeek()
  {
    ImmutableList<Integer> daysOfWeek = this.schedule.getDaysOfWeek().get();
    DateTime nextMark = this.mark;
    try
    {
      if (daysOfWeek.contains(Schedule.ALL))
      {
        // Every day
        DateTime tmp = nextMark.plusDays(1);
        if (Schedule.getAbsoluteWeekOfYear(this.mark) != Schedule.getAbsoluteWeekOfYear(tmp))
        {
          throw new IllegalFieldValueException(DateTimeFieldType.dayOfWeek(), null, null);
        }
        nextMark = tmp;
      }
      else
      {
        // Specific schedule
        if (this.curDaysOfWeekIndex == daysOfWeek.size() - 1)
        {
          // Reached the end of our specified days of the week; reset
          throw new IllegalFieldValueException(DateTimeFieldType.dayOfWeek(), null, null);
        }
        final DateTime tmp = nextMark.withDayOfWeek(daysOfWeek.get(++this.curDaysOfWeekIndex));
        if (tmp.isBefore(nextMark))
        {
          // We went back in time, which isn't a smart move.  Go forward one week to reset
          nextMark = tmp.plusWeeks(1);
        }
        else
        {
          nextMark = tmp;
        }
      }
    }
    catch (IllegalFieldValueException ifve)
    {
      // Our attempt to increment the day caused an invalid value, which
      // means that there are no more valid values for this week.  Roll the week
      // over and find the next valid value
      this.curDaysOfWeekIndex = 0;
      if (this.schedule.getWeeksOfMonth().isPresent())
      {
        nextMark = nextWeekOfMonth(this.mark);
        nextMark= resetDay(nextMark);
      }
      else if (this.schedule.getWeeksOfYear().isPresent())
      {
        nextMark = nextWeekOfYear(this.mark);
        nextMark= resetDay(nextMark);
      }
      else
      {
        throw new ServerError("Invalid schedule format (DoW but no Wo?)");
      }
    }
    this.mark = nextMark;
  }

  private DateTime nextWeekOfMonth(final DateTime mark)
  {
    // FIXME code
    throw new ServerError("TODO");
  }

  // Get the next week for our weeks-of-year schedule.
  private DateTime nextWeekOfYear(final DateTime mark)
  {
    final ImmutableList<Integer> weeksOfYear = this.schedule.getWeeksOfYear().get();
    DateTime nextMark = Schedule.withDayOfAbsoluteWeek(mark, 1);
    while (true)
    {
      try
      {
        if (weeksOfYear.contains(Schedule.ALL))
        {
          final DateTime tmp = nextMark.plusWeeks(1);
          if (mark.getYear() != tmp.getYear())
          {
            // Go to the next year
            throw new IllegalFieldValueException(DateTimeFieldType.weekOfWeekyear(), null, null);
          }
          try
          {
            nextMark = resetDay(tmp);
            break;
          }
          catch (IllegalFieldValueException ifve)
          {
            // This week doesn't match the constraints, go round the loop and try the next
            nextMark = tmp;
          }
        }
        else
        {
          // Specific schedule
          if (this.curWeeksOfYearIndex == weeksOfYear.size() - 1)
          {
            // Reached the end of our specified weeks of the year; reset
            throw new IllegalFieldValueException(DateTimeFieldType.weekOfWeekyear(), null, null);
          }
          final DateTime tmp = Schedule.withAbsoluteWeekOfYear(nextMark, weeksOfYear.get(++this.curWeeksOfYearIndex));
          try
          {
            nextMark = resetDay(tmp);
            break;
          }
          catch (IllegalFieldValueException ifve)
          {
            // This month doesn't match the constraints, go round the loop and try the next
          }
        }
      }
      catch (IllegalFieldValueException ifve)
      {
        try
        {
          // No luck just incrementing the month.  Roll the year over and find
          // the next valid value
          nextMark = nextYear(nextMark);
          nextMark = resetWeek(nextMark);
          nextMark = resetDay(nextMark);
          break;
        }
        catch (IllegalFieldValueException ifve2)
        {
          // Need to keep iterating
        }
      }
    }
    return nextMark;
  }


  // Get the next day for our days-per-month schedule
  private void nextDayOfMonth()
  {
    ImmutableList<Integer> daysOfMonth = this.schedule.getDaysOfMonth().get();
    DateTime nextMark = this.mark;
    try
    {
      if (daysOfMonth.contains(Schedule.ALL))
      {
        // Every day
        DateTime tmp = nextMark.plusDays(1);
        if (this.mark.getMonthOfYear() != tmp.getMonthOfYear())
        {
          throw new IllegalFieldValueException(DateTimeFieldType.dayOfMonth(), null, null);
        }
        nextMark = tmp;
      }
      else
      {
        // Specific schedule
        if (this.curDaysOfMonthIndex == daysOfMonth.size() - 1)
        {
          // Reached the end of our specified days of the month; reset
          throw new IllegalFieldValueException(DateTimeFieldType.dayOfMonth(), null, null);
        }
        nextMark = nextMark.withDayOfMonth(daysOfMonth.get(++this.curDaysOfMonthIndex));
      }
    }
    catch (IllegalFieldValueException ifve)
    {
      // Our attempt to increment the day caused an invalid value, which means that
      // there are no more valid values for this month.  Roll the month over and find
      // the next valid value
      this.curDaysOfMonthIndex = 0;
      nextMark = nextMonthOfYear(this.mark);
    }
    this.mark = nextMark;
  }

  private void nextDayOfYear()
  {
    final ImmutableList<Integer> daysOfYear = this.schedule.getDaysOfYear().get();
    DateTime nextMark = this.mark;
    while (true)
    {
      try
      {
        if (daysOfYear.contains(Schedule.ALL))
        {
          final DateTime tmp = nextMark.plusDays(1);
          if (mark.getYear() != tmp.getYear())
          {
            // Go to next year
            throw new IllegalFieldValueException(DateTimeFieldType.dayOfYear(), null, null);
          }
          nextMark = tmp;
          break;
        }
        else
        {
          // Specific schedule
          if (this.curDaysOfYearIndex == daysOfYear.size() - 1)
          {
            // Reached the end of our specified days of the year; reset
            throw new IllegalFieldValueException(DateTimeFieldType.dayOfYear(), null, null);
          }
          nextMark = nextMark.withDayOfYear(daysOfYear.get(++this.curDaysOfYearIndex));
          break;
        }
      }
      catch (IllegalFieldValueException ifve)
      {
        try
        {
          // Illegal value of the day, reset
          this.curDaysOfYearIndex = 0;
          nextMark = nextYear(nextMark);
          nextMark = resetDay(nextMark);
          break;
        }
        catch (IllegalFieldValueException ifve2)
        {
          // No luck; iterate
        }
      }
    }
    this.mark = nextMark;
  }

  // Get the next month for our months-of-year schedule.  Note that this is not
  // as simple as finding the next day of the month, as a value such as 31st day
  // of the month might not be valid for June but is for July
  private DateTime nextMonthOfYear(final DateTime mark)
  {
    final ImmutableList<Integer> monthsOfYear = this.schedule.getMonthsOfYear().get();
    DateTime nextMark = mark.withDayOfMonth(1);
    while (true)
    {
      try
      {
        if (monthsOfYear.contains(Schedule.ALL))
        {
          final DateTime tmp = nextMark.plusMonths(1);
          if (mark.getYear() != tmp.getYear())
          {
            // Go to the next year
            throw new IllegalFieldValueException(DateTimeFieldType.monthOfYear(), null, null);
          }
          try
          {
            nextMark = resetDay(tmp);
            break;
          }
          catch (IllegalFieldValueException ifve)
          {
            // This month doesn't match the constraints, go round the loop and try the next
            nextMark = tmp;
          }
        }
        else
        {
          // Specific schedule
          if (this.curMonthsOfYearIndex == monthsOfYear.size() - 1)
          {
            // Reached the end of our specified months of the year; reset
            throw new IllegalFieldValueException(DateTimeFieldType.monthOfYear(), null, null);
          }
          final DateTime tmp = nextMark.withMonthOfYear(monthsOfYear.get(++this.curMonthsOfYearIndex));
          try
          {
            nextMark = resetDay(tmp);
            break;
          }
          catch (IllegalFieldValueException ifve)
          {
            // This month doesn't match the constraints, go round the loop and try the next
          }
        }
      }
      catch (IllegalFieldValueException ifve)
      {
        try
        {
          // No luck just incrementing the month.  Roll the year over and find
          // the next valid value
          nextMark = nextYear(nextMark);
          nextMark = resetMonth(nextMark);
          nextMark = resetDay(nextMark);
          break;
        }
        catch (IllegalFieldValueException ifve2)
        {
          // Need to keep iterating
        }
      }
    }
    return nextMark;
  }

  // Provide the next valid year.
  // FIXME implement using the yeargap
  private DateTime nextYear(final DateTime mark)
  {
    DateTime nextMark = mark;
    nextMark = nextMark.withDayOfMonth(1).withMonthOfYear(1).plusYears(1);
    return nextMark;
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
