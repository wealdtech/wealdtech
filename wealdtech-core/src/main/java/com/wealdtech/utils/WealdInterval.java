package com.wealdtech.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import static com.wealdtech.Preconditions.checkNotNull;

/**
 * A WealdInterval is similar to a Joda {@link org.joda.time.Interval} except that it retains the timezones of both the start and
 * the end times.  It also implements {@link Comparable} as per {@link IntervalOrdering}
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

  private void validate()
  {
    checkNotNull(start, "Start must be specified");
    checkNotNull(end, "End must be specified");
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
    return Objects.hashCode(super.hashCode(),
                            this.start,
                            this.end);
  }

  public int compareTo(final WealdInterval that)
  {
    return ComparisonChain.start().compare(this.getInterval(), that.getInterval(), IntervalOrdering.INSTANCE).result();
  }
}
