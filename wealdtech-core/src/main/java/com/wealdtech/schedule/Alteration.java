package com.wealdtech.schedule;

import java.util.Locale;

import org.joda.time.Interval;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Optional;
import com.wealdtech.DataError;
import com.wealdtech.utils.StringUtils;

public class Alteration<T> implements Comparable<Alteration<T>>
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
    // TODO
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
    // TODO Auto-generated method stub
    return 0;
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
    ALTERATION,
    /**
     * Addition is a value which returns something in addition to
     * the predefined values
     */
    ADDITION;

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
