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

  private transient Integer curYearsOfScheduleIndex;
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
    if (this.schedule.getYearsOfSchedule().isPresent())
    {
      this.curYearsOfScheduleIndex = 0;
    }
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
    if (this.curDaysOfWeekIndex != null)
    {
      ImmutableList<Integer> daysOfWeek = this.schedule.getDaysOfWeek().get();
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
        this.mark = this.mark.plusDays(7 - days);
      }
    }

    // FIXME set endtime of occurrence
    return new Occurrence(this.mark, this.mark);
  }

  @Override
  public Occurrence nextAfterItem(Occurrence mark)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Occurrence nextAfter(DateTime mark)
  {
    // TODO Auto-generated method stub
    return null;
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

  @Override
  public Occurrence previousBeforeItem(Occurrence mark)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Occurrence previousBefore(DateTime mark)
  {
    // TODO Auto-generated method stub
    return null;
  }
}
