package com.wealdtech.schedule;

import java.util.Locale;

import org.joda.time.Interval;
import org.joda.time.base.BaseDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Optional;
import com.google.common.collect.ComparisonChain;
import com.wealdtech.DataError;
import com.wealdtech.utils.IntervalOrdering;
import com.wealdtech.utils.StringUtils;

import static com.wealdtech.Preconditions.*;

/**
 * An alteration is a mechanism to alter a specific entry
 * in a schedule.  Specifically, a specific entry can be considered
 * an exception and ignored, or an alteration and an alternative
 * returned in its place.
 */
public class Alteration<T extends BaseDateTime> implements Comparable<Alteration<T>>
{
  private final AlterationType type;
  private final T start;
  private final Optional<Interval> replacement;

  public Alteration(final AlterationType type, final T start, final Interval replacement)
  {
    this.type = type;
    this.start = start;
    this.replacement = Optional.fromNullable(replacement);
    validate();
  }

  private void validate()
  {
    checkNotNull(this.type, "Alteration requires a type");
    checkNotNull(this.start, "Alteration requires a start");
    if (this.type == AlterationType.EXCEPTION)
    {
      checkState(!this.replacement.isPresent(), "Exception alterations should not have a replacement");
    }
    else
    {
      checkNotNull(this.replacement, "Non-exception alterations require a replacement");
    }
  }

  public AlterationType getType()
  {
    return this.type;
  }

  public T getStart()
  {
    return this.start;
  }

  public Optional<Interval> getReplacement()
  {
    return this.replacement;
  }
  @Override
  public int compareTo(Alteration<T> that)
  {
    return ComparisonChain.start()
                          .compare(this.type, that.type)
                          .compare(this.start, that.start)
                          .compare(this.replacement.orNull(), that.replacement.orNull(), IntervalOrdering.INSTANCE.nullsFirst())
                          .result();
  }

  public enum AlterationType
  {
    /**
     * Exception is value which is not returned even though it
     * is a predefined value
     */
    EXCEPTION,
    /**
     * Alteration is a value which returns something other than
     * the predefined value
     */
    ALTERATION;

    @Override
    @JsonValue
    public String toString()
    {
        return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH));
    }

    @JsonCreator
    public static AlterationType fromString(final String alterationType)
    {
      try
      {
        return valueOf(alterationType.toUpperCase(Locale.ENGLISH));
      }
      catch (IllegalArgumentException iae)
      {
        // N.B. we don't pass the iae as the cause of this exception because
        // this happens during invocation, and in that case the enum handler
        // will report the root cause exception rather than the one we throw.
        throw new DataError.Bad("A schedule alteration type supplied is invalid"); // NOPMD
      }
    }
  }
}
