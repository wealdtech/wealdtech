package com.wealdtech;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.wealdtech.utils.StringUtils;

import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;

/**
 * A generic enum that represents four states of unknown, negative, indeterminate and positive
 */
public enum QuadState
{
  /**
   * Unknown state
   */
  UNKNOWN(1),
  /**
   * Negative state
   */
  NO(2),
  /**
   * Indeterminate state
   */
  MAYBE(3),
  /**
   * Positive state
   */
  YES(4);

  public final int val;

  private QuadState(final int val)
  {
    this.val = val;
  }

  private static final ImmutableSortedMap<Integer, QuadState> _VALMAP;
  static
  {
    final Map<Integer, QuadState> levelMap = Maps.newHashMap();
    for (final QuadState state : QuadState.values())
    {
      levelMap.put(state.val, state);
    }
    _VALMAP = ImmutableSortedMap.copyOf(levelMap);
  }

  public boolean isHigherThan(final QuadState that)
  {
    return this.val > that.val;
  }

  public boolean isLowerThan(final QuadState that)
  {
    return this.val < that.val;
  }

  @Override
  @JsonValue
  public String toString()
  {
    return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH));
  }

  @JsonCreator
  public static QuadState fromString(final String val)
  {
    try
    {
      return valueOf(val.toUpperCase(Locale.ENGLISH));
    }
    catch (final IllegalArgumentException iae)
    {
      // N.B. we don't pass the iae as the cause of this exception because
      // this happens during invocation, and in that case the enum handler
      // will report the root cause exception rather than the one we throw.
      throw new DataError.Bad("A quad state supplied is invalid");
    }
  }

  public static QuadState fromInt(final Integer val)
  {
    checkNotNull(val, "Quad state not supplied");
    final QuadState state = _VALMAP.get(val);
    checkNotNull(state, "Quadstate is invalid");
    return state;
  }
}
