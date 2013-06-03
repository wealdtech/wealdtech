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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeFieldType;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.Interval;
import org.joda.time.base.BaseDateTime;

import com.google.common.collect.ImmutableList;
import com.wealdtech.ServerError;

import static com.wealdtech.utils.Joda.*;

/**
 * A ScheduleAccessor allows access to occurrences within a schedule
 */
public class ScheduleIterator<T extends BaseDateTime> implements Iterator<Interval>
{
  private final transient Schedule<T> schedule;

  private transient DateTime mark;
  private transient boolean preset = false;

  private transient Integer curMonthsOfYearIndex;
  private transient Integer curWeeksOfYearIndex;
  private transient Integer curWeeksOfMonthIndex;
  private transient Integer curDaysOfYearIndex;
  private transient Integer curDaysOfMonthIndex;
  private transient Integer curDaysOfWeekIndex;

  private Iterator<Alteration<T>> alterationItr;
  private Alteration<T> alteration;

  /**
   * Create a new iterator starting from the schedule's start date
   * @param schedule the schedule over which to iterate
   */
  public ScheduleIterator(final Schedule<T> schedule)
  {
    this(schedule, schedule.getStart().toDateTime());
  }

  /**
   * Create a new iterator starting from a given mark
   * @param schedule the schedule over which to iterate
   * @param mark the first date in the schedule
   */
  public ScheduleIterator(final Schedule<T> schedule, final DateTime mark)
  {
    this.schedule = schedule;
    this.mark = mark;
    if (this.schedule.getAlterations().isPresent())
    {
      this.alterationItr = this.schedule.getAlterations().get().iterator();
      this.alteration = this.alterationItr.next();
    }
    else
    {
      this.alterationItr = null;
      this.alteration = null;
    }
    resetIndices();
  }

  /**
   * Reset the indices tracking our schedule according to the current mark.
   */
  private void resetIndices()
  {
    if (this.schedule.getMonthsOfYear().isPresent())
    {
      this.curMonthsOfYearIndex = this.schedule.getMonthsOfYear().get().indexOf(this.mark.getMonthOfYear());
    }
    if (this.schedule.getWeeksOfYear().isPresent())
    {
      this.curWeeksOfYearIndex = this.schedule.getWeeksOfYear().get().indexOf(this.mark.get(AbsWeekOfYear));
    }
    if (this.schedule.getWeeksOfMonth().isPresent())
    {
      this.curWeeksOfMonthIndex = 0;
    }
    if (this.schedule.getDaysOfYear().isPresent())
    {
      this.curDaysOfYearIndex = this.schedule.getDaysOfYear().get().indexOf(this.mark.getDayOfYear());
    }
    if (this.schedule.getDaysOfMonth().isPresent())
    {
      this.curDaysOfMonthIndex = this.schedule.getDaysOfMonth().get().indexOf(this.mark.getDayOfMonth());
    }
    if (this.schedule.getDaysOfWeek().isPresent())
    {
      this.curDaysOfWeekIndex = this.schedule.getDaysOfWeek().get().indexOf(this.mark.getDayOfWeek());
    }
    this.preset = true;
  }

  private DateTime resetWeek(final DateTime mark)
  {
    if (this.schedule.getWeeksOfMonth().isPresent())
    {
      return resetWeekOfMonth(mark);
    }
    else if (this.schedule.getWeeksOfYear().isPresent())
    {
      return resetWeekOfYear(mark);
    }
    else
    {
      return mark;
    }
  }

  private DateTime resetWeekOfMonth(final DateTime mark)
  {
    this.curWeeksOfMonthIndex = 0;
    return mark.withField(AbsWeekOfMonth, this.schedule.getWeeksOfMonth().get().get(0));
  }

  private DateTime resetWeekOfYear(final DateTime mark)
  {
      this.curWeeksOfYearIndex = 0;
      return mark.withField(AbsWeekOfYear, this.schedule.getWeeksOfYear().get().get(0));
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
      this.curMonthsOfYearIndex = 0;
      return mark.withMonthOfYear(this.schedule.getMonthsOfYear().get().get(0));
  }

  private DateTime resetDay(final DateTime mark)
  {
    if (this.schedule.getDaysOfWeek().isPresent())
    {
      if (this.schedule.getWeeksOfYear().isPresent())
      {
        return resetDayOfWeek(mark);
      }
      else if (this.schedule.getWeeksOfMonth().isPresent())
      {
        return resetDayOfWeekOfMonth(mark);
      }
      else
      {
        throw new ServerError("Bad schedule (DoW without Wo?)");
      }
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
  private DateTime resetDayOfWeek(final DateTime mark)
  {
    return Schedule.withDayOfAbsoluteWeek(mark, this.schedule.getDaysOfWeek().get().get(this.curDaysOfWeekIndex));
  }

  /**
   * Reset the mark to provide the correct week of the month
   */
  private DateTime resetDayOfWeekOfMonth(final DateTime mark)
  {
    return Schedule.withDayOfAbsoluteWeekOfMonth(mark, this.schedule.getDaysOfWeek().get().get(this.curDaysOfWeekIndex));
  }

  /**
   * Reset the day of the month to the first valid value
   * @throws IllegalFieldValueException if this results in an invalid date
   */
  private DateTime resetDayOfMonth(final DateTime mark) throws IllegalFieldValueException
  {
      return mark.withDayOfMonth(this.schedule.getDaysOfMonth().get().get(this.curDaysOfMonthIndex));
  }

  /**
   * Reset the day of the year to the first valid value
   * @throws IllegalFieldValueException if this results in an invalid date
   */
  private DateTime resetDayOfYear(final DateTime mark) throws IllegalFieldValueException
  {
      return mark.withDayOfYear(this.schedule.getDaysOfYear().get().get(this.curDaysOfYearIndex));
  }

  @Override
  public boolean hasNext()
  {
    if (this.schedule.terminates())
    {
      try
      {
        next();
      }
      catch (NoSuchElementException nsee)
      {
        return false;
      }
      this.preset = true;
      return true;
    }
    else
    {
      return true;
    }
  }

  @Override
  public Interval next()
  {
    Interval result = null;
    while (result == null)
    {
      if (this.preset == true)
      {
        // Use the preset value
        this.preset = false;
      }
      else
      {
        // Find the next valid day in the schedule
        nextDay();
      }
      // Mark is now valid as per the base schedule; tweak as per alterations
      updateAlteration();

      if ((this.alteration == null) || (!this.alteration.getStart().isEqual(this.mark)))
      {
        result = new Interval(this.mark, this.schedule.getDuration());
        break;
      }
      else
      {
        // Found a valid alteration, handle it
        switch (this.alteration.getType())
        {
          case EXCEPTION:
            // Ignore the exception
            break;
          case ALTERATION:
            // Change the result to fit the alteration
            result = new Interval(this.alteration.getReplacement().get());
            break;
          default:
            throw new ServerError("Unhandled alteration type " + this.alteration.getType());
        }
      }
    }
    return result;
  }

  // Update the alteration to match the next required
  private void updateAlteration()
  {
    if (this.alteration != null)
    {
      while (this.alteration.getStart().isBefore(this.mark))
      {
        try
        {
          this.alteration = this.alterationItr.next();
        }
        catch (NoSuchElementException nsee)
        {
          // End of alterations
          this.alteration = null;
          break;
        }
      }
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
    else
    {
      nextDayOfYear();
    }
    if (this.schedule.terminates())
    {
      if (this.mark.isAfter(this.schedule.getEndDate()))
      {
        throw new NoSuchElementException("No more occurrences in this schedule");
      }
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
      if (this.curDaysOfWeekIndex == daysOfWeek.size() - 1)
      {
        // Reached the end of our specified days of the week.  However, we do *not*
        // reset here because it might be that the week starts on a day other than Monday
        // and in this case we can start in the middle of the array
        this.curDaysOfWeekIndex = -1;
      }
      final DateTime tmp = nextMark.withDayOfWeek(daysOfWeek.get(++this.curDaysOfWeekIndex));
      if (tmp.equals(nextMark) || tmp.isBefore(nextMark))
      {
        // We didn't move forwards, which isn't a smart move.  Go forward one week to reset
        nextMark = tmp.plusWeeks(1);
      }
      else
      {
        nextMark = tmp;
      }
      if (((this.curWeeksOfYearIndex != null) && (this.mark.get(AbsWeekOfYear) != nextMark.get(AbsWeekOfYear))) ||
          ((this.curWeeksOfMonthIndex != null) && (this.mark.get(AbsWeekOfMonth) != nextMark.get(AbsWeekOfMonth))))
      {
        // We've moved in to next week; roll over
        throw new IllegalFieldValueException(DateTimeFieldType.dayOfWeek(), null, null);
      }
    }
    catch (IllegalFieldValueException ifve)
    {
      // Our attempt to increment the day caused an invalid value, which
      // means that there are no more valid values for this week.  Roll the week
      // over and find the next valid value
      resetDaysOfWeekIndex(nextMark);
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

  // Reset the days of the week index to the value relating
  // to the first day of the week in our schedule
  private void resetDaysOfWeekIndex(final DateTime datetime)
  {
    if (this.curDaysOfWeekIndex != null)
    {
      final int weekOfYear = datetime.get(AbsWeekOfYear);
      int firstDayOfWeek = datetime.withDayOfYear((weekOfYear - 1) * DateTimeConstants.DAYS_PER_WEEK + 1).getDayOfWeek();
      do
      {
        this.curDaysOfWeekIndex = this.schedule.getDaysOfWeek().get().indexOf(firstDayOfWeek++);
        if (firstDayOfWeek == 8)
        {
          firstDayOfWeek = 1;
        }
      }
      while (this.curDaysOfWeekIndex == -1);
    }
  }

  private DateTime nextWeekOfMonth(final DateTime mark)
  {
    final ImmutableList<Integer> weeksOfMonth = this.schedule.getWeeksOfMonth().get();
    DateTime nextMark = mark;
    while (true)
    {
      try
      {
        if (this.curWeeksOfMonthIndex == weeksOfMonth.size() - 1)
        {
          // Reached the end of our specified weeks of the month.  Reset and rollover
          throw new IllegalFieldValueException(DateTimeFieldType.monthOfYear(), null, null);
        }
        final int weeksToAdd = weeksOfMonth.get(this.curWeeksOfMonthIndex + 1) - weeksOfMonth.get(this.curWeeksOfMonthIndex++);
        nextMark = nextMark.plusWeeks(weeksToAdd);
        if (this.mark.getMonthOfYear() != nextMark.getMonthOfYear())
        {
          // We've moved in to next month; roll over
          throw new IllegalFieldValueException(DateTimeFieldType.monthOfYear(), null, null);
        }
        break;
      }
      catch (IllegalFieldValueException ifve)
      {
        try
        {
          // Our attempt to increment the week caused an invalid value, which
          // means that there are no more valid values for this month.  Roll the
          // month over and find the next valid value
          this.curWeeksOfMonthIndex = 0;
          nextMark = nextMonthOfYear(this.mark);
          nextMark = resetDay(nextMark);
          nextMark = resetWeek(nextMark);
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

  // Get the next week for our weeks-of-year schedule.
  private DateTime nextWeekOfYear(final DateTime mark)
  {
    final ImmutableList<Integer> weeksOfYear = this.schedule.getWeeksOfYear().get();
    DateTime nextMark = Schedule.withFirstDayOfAbsoluteWeek(mark);
    while (true)
    {
      try
      {
        if (this.curWeeksOfYearIndex == weeksOfYear.size() - 1)
        {
          // Reached the end of our specified weeks of the year; reset
          throw new IllegalFieldValueException(DateTimeFieldType.weekOfWeekyear(), null, null);
        }
        final DateTime tmp = nextMark.withField(AbsWeekOfYear, weeksOfYear.get(++this.curWeeksOfYearIndex));
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
      catch (IllegalFieldValueException ifve)
      {
        try
        {
          // No luck just incrementing the month.  Roll the year over and find
          // the next valid value
          nextMark = nextYear(nextMark);
          nextMark = resetDay(nextMark);
          nextMark = resetWeek(nextMark);
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
      if (this.curDaysOfMonthIndex == daysOfMonth.size() - 1)
      {
        // Reached the end of our specified days of the month; reset
        throw new IllegalFieldValueException(DateTimeFieldType.dayOfMonth(), null, null);
      }
      nextMark = nextMark.withDayOfMonth(daysOfMonth.get(++this.curDaysOfMonthIndex));
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
        if (this.curDaysOfYearIndex == daysOfYear.size() - 1)
        {
          // Reached the end of our specified days of the year; reset
          throw new IllegalFieldValueException(DateTimeFieldType.dayOfYear(), null, null);
        }
        nextMark = nextMark.withDayOfYear(daysOfYear.get(++this.curDaysOfYearIndex));
        break;
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
        if (this.curMonthsOfYearIndex == monthsOfYear.size() - 1)
        {
          // Reached the end of our specified months of the year; reset
          throw new IllegalFieldValueException(DateTimeFieldType.monthOfYear(), null, null);
        }
        final DateTime tmp = nextMark.withMonthOfYear(monthsOfYear.get(++this.curMonthsOfYearIndex));
        try
        {
          nextMark = resetDay(tmp);
          nextMark = resetWeek(nextMark);
          break;
        }
        catch (IllegalFieldValueException ifve)
        {
          // This month doesn't match the constraints, go round the loop and try the next
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

  // Provide the next valid year.
  private DateTime nextYear(final DateTime mark)
  {
    DateTime nextMark = mark;
    nextMark = nextMark.withDayOfMonth(1).withMonthOfYear(1).plusYears(1);
    return nextMark;
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException("Schedule iterators are read-only");
  }
}
