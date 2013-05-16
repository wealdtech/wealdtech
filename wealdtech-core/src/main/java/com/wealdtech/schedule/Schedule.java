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

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Period;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.wealdtech.utils.Accessor;
import com.wealdtech.utils.PeriodOrdering;

import static com.wealdtech.Preconditions.*;

/**
 * A Schedule is a statement of one or more {@link Occurrence} instances.
 * <p/>A simple schedule will contain a single start and end time.
 * More complex schedules will include recurrence on some set pattern,
 * resulting in multiple occurrences.  A recurring schedule may or may not
 * have an end time.
 */
public class Schedule implements Comparable<Schedule>
{
  public static Integer ALL = new Integer(0);

  private final DateTime start;
  private final Optional<DateTime> end;

  private final Period duration;

  private final Optional<ImmutableList<Integer>> yearsOfSchedule;
  private final Optional<ImmutableList<Integer>> monthsOfYear;
  private final Optional<ImmutableList<Integer>> weeksOfYear;
  private final Optional<ImmutableList<Integer>> weeksOfMonth;
  private final Optional<ImmutableList<Integer>> daysOfYear;
  private final Optional<ImmutableList<Integer>> daysOfMonth;
  private final Optional<ImmutableList<Integer>> daysOfWeek;

  // TODO times

  private Schedule(final DateTime start,
                            final DateTime end,
                            final Period duration,
                            final ImmutableList<Integer> yearsOfSchedule,
                            final ImmutableList<Integer> monthsOfYear,
                            final ImmutableList<Integer> weeksOfYear,
                            final ImmutableList<Integer> weeksOfMonth,
                            final ImmutableList<Integer> daysOfYear,
                            final ImmutableList<Integer> daysOfMonth,
                            final ImmutableList<Integer> daysOfWeek)
  {
    this.start = start;
    this.end = Optional.fromNullable(end);
    this.duration = duration;
    // Empty lists count as absent
    // FIXME order lists by value
    if (yearsOfSchedule == null || yearsOfSchedule.isEmpty())
    {
      this.yearsOfSchedule = Optional.absent();
    }
    else
    {
      this.yearsOfSchedule = Optional.of(yearsOfSchedule);
    }
    if (monthsOfYear == null || monthsOfYear.isEmpty())
    {
      this.monthsOfYear = Optional.absent();
    }
    else
    {
      this.monthsOfYear = Optional.of(monthsOfYear);
    }
    if (weeksOfYear == null || weeksOfYear.isEmpty())
    {
      this.weeksOfYear = Optional.absent();
    }
    else
    {
      this.weeksOfYear = Optional.of(weeksOfYear);
    }
    if (weeksOfMonth == null || weeksOfMonth.isEmpty())
    {
      this.weeksOfMonth = Optional.absent();
    }
    else
    {
      this.weeksOfMonth = Optional.of(weeksOfMonth);
    }
    if (daysOfYear == null || daysOfYear.isEmpty())
    {
      this.daysOfYear = Optional.absent();
    }
    else
    {
      this.daysOfYear = Optional.of(daysOfYear);
    }
    if (daysOfMonth == null || daysOfMonth.isEmpty())
    {
      this.daysOfMonth = Optional.absent();
    }
    else
    {
      this.daysOfMonth = Optional.of(daysOfMonth);
    }
    if (daysOfWeek == null || daysOfWeek.isEmpty())
    {
      this.daysOfWeek = Optional.absent();
    }
    else
    {
      this.daysOfWeek = Optional.of(daysOfWeek);
    }
    validate();
  }

  // Here we attempt to validate the schedule such that it is deterministic
  // and will always provide values
  private void validate()
  {
    checkNotNull(this.start, "Schedule requires a start");

    checkNotNull(this.duration, "Schedule requires a duration");

    if (this.yearsOfSchedule.isPresent())
    {
      // TODO Confirm that all values are >= 0
    }

    if (this.monthsOfYear.isPresent())
    {
      // This is a month-based schedule so ensure that there are no year-based
      checkState(!this.weeksOfYear.isPresent(), "Cannot combine yearly and weekly schedules");
      checkState(!this.daysOfYear.isPresent(), "Cannot combine yearly and daily schedules");
    }

    if (this.weeksOfYear.isPresent())
    {
      checkState((!this.weeksOfMonth.isPresent()), "Cannot combine yearly and weekly schedules");
      checkState((!this.daysOfYear.isPresent()), "Cannot combine yearly and daily schedules");
      checkState((!this.daysOfMonth.isPresent()), "Cannot combine yearly and daily schedules");
    }

    if (this.weeksOfMonth.isPresent())
    {
      checkState((!this.daysOfYear.isPresent()), "Cannot combine monthly and daily schedules");
      checkState((!this.daysOfMonth.isPresent()), "Cannot combine monthly and daily schedules");
    }

    if (this.daysOfYear.isPresent())
    {
      checkState((!this.daysOfMonth.isPresent()), "Cannot combine yearly and monthly schedules");
    }

    checkState(this.daysOfYear.isPresent() || this.daysOfMonth.isPresent() || this.daysOfWeek.isPresent(), "Need a day");

    // TODO Ensure that the start matches a valid date
  }

  /**
   * Returns {@code true} if this schedule terminates <em>i.e.</em> it has an end date
   * @return {@code true} if the schedule terminates
   */
  public boolean terminates()
  {
    return (this.end.isPresent());
  }

  public Accessor<Occurrence, DateTime> accessor()
  {
    return new ScheduleAccessor(this);
  }

  public DateTime getStart()
  {
    return this.start;
  }

  public Optional<DateTime> getEnd()
  {
    return this.end;
  }

  public Period getDuration()
  {
    return this.duration;
  }

  public Optional<ImmutableList<Integer>> getYearsOfSchedule()
  {
    return this.yearsOfSchedule;
  }

  public Optional<ImmutableList<Integer>> getMonthsOfYear()
  {
    return this.monthsOfYear;
  }

  public Optional<ImmutableList<Integer>> getWeeksOfYear()
  {
    return this.weeksOfYear;
  }

  public Optional<ImmutableList<Integer>> getWeeksOfMonth()
  {
    return this.weeksOfMonth;
  }

  public Optional<ImmutableList<Integer>> getDaysOfYear()
  {
    return this.daysOfYear;
  }

  public Optional<ImmutableList<Integer>> getDaysOfMonth()
  {
    return this.daysOfMonth;
  }

  public Optional<ImmutableList<Integer>> getDaysOfWeek()
  {
    return this.daysOfWeek;
  }

  // Standard object methods follow
  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
                  .add("start", this.getStart())
                  .add("end", this.getEnd())
                  .add("duration", this.getDuration())
                  .add("yearsOfSchedule", this.getYearsOfSchedule())
                  .add("monthsOfYear", this.getMonthsOfYear())
                  .add("weeksOfYear", this.getWeeksOfYear())
                  .add("weeksOfMonth", this.getWeeksOfMonth())
                  .add("daysOfYear", this.getDaysOfYear())
                  .add("daysOfMonth", this.getDaysOfMonth())
                  .add("daysOfWeek", this.getDaysOfWeek())
                  .omitNullValues()
                  .toString();
  }

  @Override
  public boolean equals(final Object that)
  {
    return that instanceof Schedule && this.compareTo((Schedule)that) == 0;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(this.getStart(),
                            this.getEnd(),
                            this.getDuration(),
                            this.getYearsOfSchedule(),
                            this.getMonthsOfYear(),
                            this.getWeeksOfYear(),
                            this.getWeeksOfMonth(),
                            this.getDaysOfYear(),
                            this.getDaysOfMonth(),
                            this.getDaysOfWeek());
  }

  @Override
  public int compareTo(final Schedule that)
  {
    return ComparisonChain.start()
                          .compare(this.getStart(), that.getStart())
                          .compare(this.getEnd().orNull(), that.getEnd().orNull(), Ordering.natural().nullsFirst())
                          .compare(this.getDuration(), that.getDuration(), new PeriodOrdering().nullsFirst())
                          .compare(this.getYearsOfSchedule().orNull(), that.getYearsOfSchedule().orNull(), Ordering.<Integer>natural().lexicographical().nullsFirst())
                          .compare(this.getMonthsOfYear().orNull(), that.getMonthsOfYear().orNull(), Ordering.<Integer>natural().lexicographical().nullsFirst())
                          .compare(this.getWeeksOfYear().orNull(), that.getWeeksOfYear().orNull(), Ordering.<Integer>natural().lexicographical().nullsFirst())
                          .compare(this.getWeeksOfMonth().orNull(), that.getWeeksOfMonth().orNull(), Ordering.<Integer>natural().lexicographical().nullsFirst())
                          .compare(this.getDaysOfYear().orNull(), that.getDaysOfYear().orNull(), Ordering.<Integer>natural().lexicographical().nullsFirst())
                          .compare(this.getDaysOfMonth().orNull(), that.getDaysOfMonth().orNull(), Ordering.<Integer>natural().lexicographical().nullsFirst())
                          .compare(this.getDaysOfWeek().orNull(), that.getDaysOfWeek().orNull(), Ordering.<Integer>natural().lexicographical().nullsFirst())
                          .result();
  }

  public static class Builder
  {
    private transient DateTime start;
    private transient DateTime end;
    private transient Period duration;
    private transient ImmutableList<Integer> yearsOfSchedule;
    private transient ImmutableList<Integer> monthsOfYear;
    private transient ImmutableList<Integer> weeksOfYear;
    private transient ImmutableList<Integer> weeksOfMonth;
    private transient ImmutableList<Integer> daysOfYear;
    private transient ImmutableList<Integer> daysOfMonth;
    private transient ImmutableList<Integer> daysOfWeek;

    public Builder()
    {
      // Nothing to do
    }

    public Builder(final Schedule prior)
    {
      this.start = prior.getStart();
      this.end = prior.getEnd().orNull();
      this.duration = prior.getDuration();
      this.yearsOfSchedule = prior.getYearsOfSchedule().orNull();
      this.monthsOfYear = prior.getMonthsOfYear().orNull();
      this.weeksOfYear = prior.getWeeksOfYear().orNull();
      this.weeksOfMonth = prior.getWeeksOfMonth().orNull();
      this.daysOfYear = prior.getDaysOfYear().orNull();
      this.daysOfMonth = prior.getDaysOfMonth().orNull();
      this.daysOfWeek= prior.getDaysOfWeek().orNull();
    }

    public Builder start(final DateTime start)
    {
      this.start = start;
      return this;
    }

    public Builder end(final DateTime end)
    {
      this.end = end;
      return this;
    }

    public Builder duration(final Period duration)
    {
      this.duration = duration;
      return this;
    }

    public Builder yearOfSchedule(final Integer yearOfSchedule)
    {
      this.yearsOfSchedule = ImmutableList.of(yearOfSchedule);
      return this;
    }

    public Builder yearsOfSchedule(final List<Integer> yearsOfSchedule)
    {
      this.yearsOfSchedule = ImmutableList.copyOf(yearsOfSchedule);
      return this;
    }

    public Builder monthOfYear(final Integer monthOfYear)
    {
      this.monthsOfYear = ImmutableList.of(monthOfYear);
      return this;
    }

    public Builder monthsOfYear(final List<Integer> monthsOfYear)
    {
      this.monthsOfYear = ImmutableList.copyOf(monthsOfYear);
      return this;
    }

    public Builder weekOfYear(final Integer weekOfYear)
    {
      this.weeksOfYear = ImmutableList.of(weekOfYear);
      return this;
    }

    public Builder weeksOfYear(final List<Integer> weeksOfYear)
    {
      this.weeksOfYear = ImmutableList.copyOf(weeksOfYear);
      return this;
    }

    public Builder weekOfMonth(final Integer weekOfMonth)
    {
      this.weeksOfMonth = ImmutableList.of(weekOfMonth);
      return this;
    }

    public Builder weeksOfMonth(final List<Integer> weeksOfMonth)
    {
      this.weeksOfMonth = ImmutableList.copyOf(weeksOfMonth);
      return this;
    }

    public Builder dayOfYear(final Integer dayOfYear)
    {
      this.daysOfYear = ImmutableList.of(dayOfYear);
      return this;
    }

    public Builder daysOfYear(final List<Integer> daysOfYear)
    {
      this.daysOfYear = ImmutableList.copyOf(daysOfYear);
      return this;
    }

    public Builder dayOfMonth(final Integer dayOfMonth)
    {
      this.daysOfMonth = ImmutableList.of(dayOfMonth);
      return this;
    }

    public Builder daysOfMonth(final List<Integer> daysOfMonth)
    {
      this.daysOfMonth = ImmutableList.copyOf(daysOfMonth);
      return this;
    }

    public Builder dayOfWeek(final Integer dayOfWeek)
    {
      this.daysOfWeek = ImmutableList.of(dayOfWeek);
      return this;
    }

    public Builder daysOfWeek(final List<Integer> daysOfWeek)
    {
      this.daysOfWeek = ImmutableList.copyOf(daysOfWeek);
      return this;
    }

    public Schedule build()
    {
      return new Schedule(this.start, this.end, this.duration, this.yearsOfSchedule, this.monthsOfYear, this.weeksOfYear, this.weeksOfMonth, this.daysOfYear, this.daysOfMonth, this.daysOfWeek);
    }
  }
}
