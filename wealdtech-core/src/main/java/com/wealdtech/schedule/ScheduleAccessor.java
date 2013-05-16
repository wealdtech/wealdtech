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

import com.wealdtech.utils.Accessor;

/**
 * A ScheduleAccessor allows access to occurrences within a schedule
 */
public class ScheduleAccessor implements Accessor<Occurrence, DateTime>
{
  private final transient Schedule schedule;

  private transient DateTime mark;

  public ScheduleAccessor(final Schedule schedule)
  {
    this(schedule, schedule.getStart());
  }

  public ScheduleAccessor(final Schedule schedule, final DateTime mark)
  {
    this.schedule = schedule;
    this.mark = mark;
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
    return this.schedule.terminates();
  }

  @Override
  public Occurrence next()
  {
    // TODO Auto-generated method stub
    return null;
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
