package com.wealdtech.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Range;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;

import static com.wealdtech.Preconditions.checkNotNull;
import static com.wealdtech.Preconditions.checkState;

/**
 * A WealdInterval is similar to a Joda {@link org.joda.time.Interval} except that it retains the timezones of both the start and
 * the end times.  It also implements {@link Comparable} as per {@link IntervalOrdering}.
 * <p/>
 * A WealdInterval is considered to be a closed-open range, and as such the end must be after the start.
 */
public class WealdInterval implements Comparable<WealdInterval>
{
  private final transient DateTime start;
  private final transient DateTime end;

  @JsonCreator
  public WealdInterval(@JsonProperty("start") final DateTime start,
                       @JsonProperty("end") final DateTime end)
  {
    this.start = start;
    this.end = end;
    validate();
  }

  public WealdInterval(final DateTime start,
                       final Duration duration)
  {
    this.start = start;
    this.end = start.plus(duration);
  }

  public WealdInterval(final DateTime start,
                       final Period period)
  {
    this.start = start;
    this.end = start.plus(period);
  }

  public static WealdInterval fromRange(final Range<DateTime> range)
  {
    return new WealdInterval(range.lowerEndpoint(), range.upperEndpoint());
  }

  private void validate()
  {
    checkNotNull(start, "Start must be specified");
    checkNotNull(end, "End must be specified");
    checkState(!end.isBefore(start), "End cannot be before start");
  }

  public DateTime getStart()
  {
    return this.start;
  }

  public DateTime getEnd()
  {
    return this.end;
  }

  /**
   * Obtain a Joda {@link Interval} from this Weald interval.
   * <p/>
   * <b>N.B.</b>
   * This loses information about the timezone in which the end datetime resides so should be used with caution.
   * @return A Joda interval
   */
  @JsonIgnore
  public Interval getInterval()
  {
    return new Interval(this.start, this.end);
  }

  public Range<DateTime> toRange()
  {
    return Range.closedOpen(this.start, this.end);
  }

  // Standard object methods follow
  @Override
  public String toString()
  {
    return Objects.toStringHelper(this).add("start", this.start).add("end", this.end).toString();
  }

  @Override
  public boolean equals(final Object that)
  {
    return that instanceof WealdInterval && this.compareTo((WealdInterval)that) == 0;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(this.start,
                            this.end);
  }

  public int compareTo(final WealdInterval that)
  {
    return ComparisonChain.start().compare(this.getInterval(), that.getInterval(), IntervalOrdering.INSTANCE).result();
  }
}
