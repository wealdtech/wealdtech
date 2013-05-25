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

import static com.wealdtech.Preconditions.*;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Period;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.wealdtech.utils.Accessor;
import com.wealdtech.utils.PeriodOrdering;

/**
 * A Schedule is a statement of one or more {@link Occurrence}s.
 * <p/>A simple schedule will contain a single start and end time,
 * referencing a single occurrence.  More complex schedules will include
 * recurrence on some set pattern resulting in multiple occurrences.
 * <p/>A schedule must have a day component, be it day of week, day of
 * month, or day of year.
 * <p/>If a schedule has a day of month component it must also have a
 * month of year component.
 * <p/>If a schedule has a day of week component it must also have either
 * a week of year component or both a week of month component and a month
 * of year component.
 * <p/>A recurring schedule may or may not have an end time.  If it does not
 * then it means that the schedule does not terminate.
 */
public class Schedule implements Comparable<Schedule>
{
  /**
   * Used when creating schedules to specify that all valid values should be included
   */
  public static Integer ALL = new Integer(0);

  // Arrays containing all days of week/months of the year/etc.
  private static final ImmutableList<Integer> ALL_DAYS_OF_WEEK = ContiguousSet.create(Range.closed(1, 7), DiscreteDomain.integers()).asList();
  private static final ImmutableList<Integer> ALL_WEEKS_OF_YEAR = ContiguousSet.create(Range.closed(1, 53), DiscreteDomain.integers()).asList();
  private static final ImmutableList<Integer> ALL_WEEKS_OF_MONTH = ContiguousSet.create(Range.closed(1, 5), DiscreteDomain.integers()).asList();
  private static final ImmutableList<Integer> ALL_DAYS_OF_MONTH = ContiguousSet.create(Range.closed(1, 31), DiscreteDomain.integers()).asList();
  private static final ImmutableList<Integer> ALL_MONTHS_OF_YEAR = ContiguousSet.create(Range.closed(1, 12), DiscreteDomain.integers()).asList();
  private static final ImmutableList<Integer> ALL_DAYS_OF_YEAR = ContiguousSet.create(Range.closed(1, 366), DiscreteDomain.integers()).asList();

  private final DateTime start;
  private final Optional<DateTime> end;

  private final Period duration;

  private final Optional<ImmutableList<Integer>> monthsOfYear;
  private final Optional<ImmutableList<Integer>> weeksOfYear;
  private final Optional<ImmutableList<Integer>> weeksOfMonth;
  private final Optional<ImmutableList<Integer>> daysOfYear;
  private final Optional<ImmutableList<Integer>> daysOfMonth;
  private final Optional<ImmutableList<Integer>> daysOfWeek;

  private Schedule(final DateTime start,
                            final DateTime end,
                            final Period duration,
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
    if (monthsOfYear == null || monthsOfYear.isEmpty())
    {
      this.monthsOfYear = Optional.absent();
    }
    else
    {
      if (monthsOfYear.contains(ALL))
      {
        // Replace an ALL schedule with the actual values.  Makes later computation much simpler
        // as there is only a single code path (everything uses the list)
        this.monthsOfYear = Optional.of(ALL_MONTHS_OF_YEAR);
      }
      else
      {
        this.monthsOfYear = Optional.of(ImmutableList.copyOf(Ordering.natural().immutableSortedCopy(monthsOfYear)));
      }
    }
    if (weeksOfYear == null || weeksOfYear.isEmpty())
    {
      this.weeksOfYear = Optional.absent();
    }
    else
    {
      if (weeksOfYear.contains(ALL))
      {
        this.weeksOfYear = Optional.of(ALL_WEEKS_OF_YEAR);
      }
      else
      {
        this.weeksOfYear = Optional.of(ImmutableList.copyOf(Ordering.natural().immutableSortedCopy(weeksOfYear)));
      }
    }
    if (weeksOfMonth == null || weeksOfMonth.isEmpty())
    {
      this.weeksOfMonth = Optional.absent();
    }
    else
    {
      if (weeksOfMonth.contains(ALL))
      {
        this.weeksOfMonth = Optional.of(ALL_WEEKS_OF_MONTH);
      }
      else
      {
        this.weeksOfMonth = Optional.of(ImmutableList.copyOf(Ordering.natural().immutableSortedCopy(weeksOfMonth)));
      }
    }
    if (daysOfYear == null || daysOfYear.isEmpty())
    {
      this.daysOfYear = Optional.absent();
    }
    else
    {
      if (daysOfYear.contains(ALL))
      {
        this.daysOfYear = Optional.of(ALL_DAYS_OF_YEAR);
      }
      else
      {
        this.daysOfYear = Optional.of(ImmutableList.copyOf(Ordering.natural().immutableSortedCopy(daysOfYear)));
      }
    }
    if (daysOfMonth == null || daysOfMonth.isEmpty())
    {
      this.daysOfMonth = Optional.absent();
    }
    else
    {
      if (daysOfMonth.contains(ALL))
      {
        this.daysOfMonth = Optional.of(ALL_DAYS_OF_MONTH);
      }
      else
      {
        this.daysOfMonth = Optional.of(ImmutableList.copyOf(Ordering.natural().immutableSortedCopy(daysOfMonth)));
      }
    }
    if (daysOfWeek == null || daysOfWeek.isEmpty())
    {
      this.daysOfWeek = Optional.absent();
    }
    else
    {
      if (daysOfWeek.contains(ALL))
      {
        this.daysOfWeek = Optional.of(ALL_DAYS_OF_WEEK);
      }
      else
      {
        this.daysOfWeek = Optional.of(ImmutableList.copyOf(Ordering.natural().immutableSortedCopy(daysOfWeek)));
      }
    }
    validate();
  }

  // Here we attempt to validate the schedule such that it is deterministic
  // and will always provide values
  private void validate()
  {
    checkNotNull(this.start, "Schedule requires a start");

    checkNotNull(this.duration, "Schedule requires a duration");

    checkState(this.daysOfYear.isPresent() || this.daysOfMonth.isPresent() || this.daysOfWeek.isPresent(), "Schedule requires a day");

    if (this.daysOfWeek.isPresent())
    {
      checkState(Collections2.filter(this.daysOfWeek.get(), Range.<Integer>lessThan(0)).isEmpty(), "Days of week must not contain negative values");
      checkState(Collections2.filter(this.daysOfWeek.get(), Range.<Integer>greaterThan(DateTimeConstants.DAYS_PER_WEEK)).isEmpty(), "Days of week must not contain values greater than 7");
      checkState((this.weeksOfMonth.isPresent() && this.monthsOfYear.isPresent()) || this.weeksOfYear.isPresent(), "Schedule not complete");
      checkState(!(this.daysOfMonth.isPresent() || this.daysOfYear.isPresent()), "Schedule cannot have multiple day items");
      checkState(!(this.weeksOfMonth.isPresent() && this.weeksOfYear.isPresent()), "Schedule cannot have multiple week items");
    }

    if (this.daysOfMonth.isPresent())
    {
      checkState(Collections2.filter(this.daysOfMonth.get(), Range.<Integer>lessThan(0)).isEmpty(), "Days of month must not contain negative values");
      checkState(Collections2.filter(this.daysOfMonth.get(), Range.<Integer>greaterThan(31)).isEmpty(), "Days of month must not contain values greater than 31");
      checkState(this.monthsOfYear.isPresent(), "Schedule not complete");
      checkState(!this.daysOfYear.isPresent(), "Schedule cannot have multiple day items");
    }

    if (this.daysOfYear.isPresent())
    {
      checkState(Collections2.filter(this.daysOfYear.get(), Range.<Integer>lessThan(0)).isEmpty(), "Days of year must not contain negative values");
      checkState(Collections2.filter(this.daysOfYear.get(), Range.<Integer>greaterThan(366)).isEmpty(), "Days of year must not contain values greater than 366");
    }

    if (this.weeksOfMonth.isPresent())
    {
      checkState(Collections2.filter(this.weeksOfMonth.get(), Range.<Integer>greaterThan(5)).isEmpty(), "Weeks of month must not contain values greater than 4");
    }

    if (this.weeksOfYear.isPresent())
    {
      checkState(Collections2.filter(this.weeksOfYear.get(), Range.<Integer>greaterThan(53)).isEmpty(), "Weeks of year must not contain values greater than 53");
    }

    if (this.monthsOfYear.isPresent())
    {
      checkState(Collections2.filter(this.monthsOfYear.get(), Range.<Integer>greaterThan(12)).isEmpty(), "Months of year must not contain values greater than 12");
    }

    checkState(isAScheduleStart(this.start), "Start date is not a valid schedule start date");
  }

  /**
   * Returns {@code true} if this schedule terminates <em>i.e.</em> it has an end date
   * @return {@code true} if the schedule terminates
   */
  public boolean terminates()
  {
    return (this.end.isPresent());
  }

  /**
   * Provide an {@link Accessor} for this schedule.
   * @return an accessor
   */
  public Accessor<Occurrence, DateTime> accessor()
  {
    return new ScheduleAccessor(this);
  }

  /**
   * Confirm if a given date/time is a valid start time for this schedule
   * @param datetime the date/time to check
   * @return {@code true} if valid, {@code false} if not
   */
  public boolean isAScheduleStart(final DateTime datetime)
  {
    // Check time
    if ((datetime.getMinuteOfHour() != this.start.getMinuteOfHour()) ||
        (datetime.getHourOfDay() != this.start.getHourOfDay()))
    {
      return false;
    }

    if (this.daysOfWeek.isPresent())
    {
      if (!this.daysOfWeek.get().contains(datetime.getDayOfWeek()))
      {
        return false;
      }
    }

    if (this.daysOfMonth.isPresent())
    {
      if (!this.daysOfMonth.get().contains(datetime.getDayOfMonth()))
      {
        return false;
      }
    }

    if (this.daysOfYear.isPresent())
    {
      if (!this.daysOfYear.get().contains(datetime.getDayOfYear()))
      {
        return false;
      }
    }

    if (this.weeksOfMonth.isPresent())
    {
      if (!this.weeksOfMonth.get().contains(Schedule.getAbsoluteWeekOfMonth(datetime)))
      {
        return false;
      }
    }

    if (this.weeksOfYear.isPresent())
    {
      if (!this.weeksOfYear.get().contains(Schedule.getAbsoluteWeekOfYear(datetime)))
      {
        return false;
      }
    }

    // All checks passed
    return true;
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

  /**
   * Calculate the absolute week of the year.
   * Absolute weeks work on the 1st of January being the first day of the
   * first week of the year.
   * <p/>Note that due to leap years it is possible for dates after
   * 28th February to be in different relative weeks in different years.
   * @param datetime
   * @return the absolute week of the year, in the range 1 to 53
   */
  public static int getAbsoluteWeekOfYear(final DateTime datetime)
  {
    return 1 + (datetime.getDayOfYear() - 1) / DateTimeConstants.DAYS_PER_WEEK;
  }

  /**
   * Set the day of the week using the absolute week definition
   * as per {@link getAbsoluteWeekOfYear}.
   * @param datetime
   * @param dayOfWeek The day of the week to set, Monday being 1 and Sunday being 7
   * @return a datetime with the day of the week set accordingly
   */
  public static DateTime withDayOfAbsoluteWeek(final DateTime datetime, final int dayOfWeek)
  {
    final int weekOfYear = getAbsoluteWeekOfYear(datetime);
    final int firstDayOfWeek = datetime.withDayOfYear((weekOfYear - 1) * DateTimeConstants.DAYS_PER_WEEK + 1).getDayOfWeek();
    final int dayOfWeekOffset = ((DateTimeConstants.DAYS_PER_WEEK - firstDayOfWeek) + dayOfWeek) % DateTimeConstants.DAYS_PER_WEEK;
    return datetime.withDayOfYear((weekOfYear - 1) * DateTimeConstants.DAYS_PER_WEEK + 1 + dayOfWeekOffset);
  }

  /**
   * Set the day to the first day of the week using the absolute week definition
   * as per {@link getAbsoluteWeekOfYear}.
   * @param datetime
   * @return
   */
  public static DateTime withFirstDayOfAbsoluteWeek(final DateTime datetime)
  {
    final int weekOfYear = getAbsoluteWeekOfYear(datetime);
    return datetime.withDayOfYear((weekOfYear - 1) * DateTimeConstants.DAYS_PER_WEEK + 1);
  }

  public static DateTime withAbsoluteWeekOfYear(final DateTime datetime, final int week)
  {
    final int dayOfWeek = datetime.getDayOfYear() - ((getAbsoluteWeekOfYear(datetime) - 1) * DateTimeConstants.DAYS_PER_WEEK);
    return datetime.withDayOfYear((week - 1) * DateTimeConstants.DAYS_PER_WEEK + dayOfWeek);
  }

  public static DateTime withAbsoluteWeekOfMonth(final DateTime datetime, final int week)
  {
    int dayOfWeek = datetime.getDayOfWeek();
    int firstDayOfMonth = datetime.withDayOfMonth(1).getDayOfWeek();
    int offset = dayOfWeek + 1 - firstDayOfMonth;
    if (offset <= 0)
    {
      offset += 7;
    }
    return datetime.withDayOfMonth((week - 1) * DateTimeConstants.DAYS_PER_WEEK + offset);
  }

  public static int getAbsoluteWeekOfMonth(final DateTime datetime)
  {
    int days = datetime.getDayOfYear() - datetime.withDayOfMonth(1).getDayOfYear();
    return 1 + days / DateTimeConstants.DAYS_PER_WEEK;
  }

  public static DateTime withDayOfAbsoluteWeekOfMonth(final DateTime datetime, final int dayOfWeek)
  {
    final int weekOfMonth = getAbsoluteWeekOfMonth(datetime);
    final int firstDayOfWeek = datetime.withDayOfMonth(1).getDayOfWeek();
    final int dayOfWeekOffset = ((DateTimeConstants.DAYS_PER_WEEK - firstDayOfWeek) + dayOfWeek) % DateTimeConstants.DAYS_PER_WEEK;
    return datetime.withDayOfMonth((weekOfMonth - 1) * DateTimeConstants.DAYS_PER_WEEK + 1 + dayOfWeekOffset);
  }

  // Standard object methods follow
  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
                  .add("start", this.getStart())
                  .add("end", this.getEnd())
                  .add("duration", this.getDuration())
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

    public Builder monthsOfYear(final Integer firstMonthOfYear, final Integer ... subsequentMonthsOfYear)
    {
      this.monthsOfYear = ImmutableList.copyOf(Lists.asList(firstMonthOfYear, subsequentMonthsOfYear));
      return this;
    }

    public Builder monthsOfYear(final List<Integer> monthsOfYear)
    {
      if (monthsOfYear == null)
      {
        this.monthsOfYear = null;
      }
      else
      {
        this.monthsOfYear = ImmutableList.copyOf(monthsOfYear);
      }
      return this;
    }

    public Builder weeksOfYear(final Integer firstWeekOfYear, final Integer ... subsequentWeeksOfYear)
    {
      this.weeksOfYear = ImmutableList.copyOf(Lists.asList(firstWeekOfYear, subsequentWeeksOfYear));
      return this;
    }

    public Builder weeksOfYear(final List<Integer> weeksOfYear)
    {
      if (weeksOfYear == null)
      {
        this.weeksOfYear = null;
      }
      else
      {
        this.weeksOfYear = ImmutableList.copyOf(weeksOfYear);
      }
      return this;
    }

    public Builder weeksOfMonth(final Integer firstWeekOfMonth, final Integer ... subsequentWeeksOfMonth)
    {
      this.weeksOfMonth = ImmutableList.copyOf(Lists.asList(firstWeekOfMonth, subsequentWeeksOfMonth));
      return this;
    }

    public Builder weeksOfMonth(final List<Integer> weeksOfMonth)
    {
      if (weeksOfMonth == null)
      {
        this.weeksOfMonth = null;
      }
      else
      {
        this.weeksOfMonth = ImmutableList.copyOf(weeksOfMonth);
      }
      return this;
    }

    public Builder daysOfYear(final Integer firstDayOfYear, final Integer ... subsequentDaysOfYear)
    {
      this.daysOfYear = ImmutableList.copyOf(Lists.asList(firstDayOfYear, subsequentDaysOfYear));
      return this;
    }

    public Builder daysOfYear(final List<Integer> daysOfYear)
    {
      if (daysOfYear == null)
      {
        this.daysOfYear = null;
      }
      else
      {
        this.daysOfYear = ImmutableList.copyOf(daysOfYear);
      }
      return this;
    }

    public Builder daysOfMonth(final Integer firstDayOfMonth, final Integer ... subsequentDaysOfMonth)
    {
      this.daysOfMonth = ImmutableList.copyOf(Lists.asList(firstDayOfMonth, subsequentDaysOfMonth));
      return this;
    }

    public Builder daysOfMonth(final List<Integer> daysOfMonth)
    {
      if (daysOfMonth == null)
      {
        this.daysOfMonth = null;
      }
      else
      {
        this.daysOfMonth = ImmutableList.copyOf(daysOfMonth);
      }
      return this;
    }

    public Builder daysOfWeek(final Integer firstDayOfWeek, final Integer ... subsequentDaysOfWeek)
    {
      this.daysOfWeek = ImmutableList.copyOf(Lists.asList(firstDayOfWeek, subsequentDaysOfWeek));
      return this;
    }

    public Builder daysOfWeek(final List<Integer> daysOfWeek)
    {
      if (daysOfWeek == null)
      {
        this.daysOfWeek = null;
      }
      else
      {
        this.daysOfWeek = ImmutableList.copyOf(daysOfWeek);
      }
      return this;
    }

    public Schedule build()
    {
      return new Schedule(this.start, this.end, this.duration, this.monthsOfYear, this.weeksOfYear, this.weeksOfMonth, this.daysOfYear, this.daysOfMonth, this.daysOfWeek);
    }
  }
}
