package com.wealdtech;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * A simple two-tuple.
 */
public class TwoTuple<S, T>
{
  private final S s;
  private final T t;

  public static <P, Q> TwoTuple<P, Q> build(final P p, final Q q)
  {
    return new TwoTuple<P, Q>(p, q);
  }

  public S getS()
  {
    return s;
  }

  public T getT()
  {
    return t;
  }

  @JsonCreator
  public TwoTuple(@JsonProperty("s") final S s,
                  @JsonProperty("t") final T t)
  {
      this.s = s;
      this.t = t;
  }

  // Standard object methods follow
  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
                  .add("s", this.s)
                  .add("t", this.t)
                  .omitNullValues()
                  .toString();
  }

  @Override
  public boolean equals(final Object that)
  {
    if (that == null)
    {
      return false;
    }
    if (!(that instanceof TwoTuple))
    {
      return false;
    }
    final TwoTuple<S, T>tthat = (TwoTuple<S, T>)that;
    return Objects.equal(this.s, tthat.s) && Objects.equal(this.t, tthat.t);
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(this.s,
                            this.t);
  }
}
