package com.wealdtech.utils;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wealdtech.DataError;

import static com.wealdtech.Preconditions.*;

/**
 * EnvironmentType is used to provide a high level indicator of the type of environment in which we are operating.  It is commonly
 * used as a switch in configuration files and generators to provide environment-specific information
 */
public enum EnvironmentType
{
  /**
   * Development is an environment which is used for developing new code
   */
  DEVELOPMENT,
  /**
   * Test is an environment which is a staging area prior to production, or used to track down issues in production which are
   * not easily reproducible without altering data
   */
  TEST,
  /**
   * Test standby is an environment which is usually inactive but ready to take over the duties of the test environment if
   * required.  Used for testing standby operations
   */
  TESTSTANDBY,
  /**
   * Production is an environment which is contains live data and is used for the real operation of the system
   */
  PRODUCTION,
  /**
   * Production standby is an environment which is usually inactive but ready to take over the duties of the production
   * environment if required
   */
  PROUDUCTIONSTANDBY;

  @Override
  @JsonValue
  public String toString()
  {
    return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH));
  }

  @JsonCreator
  public static EnvironmentType fromString(final String type)
  {
    checkNotNull(type, "Environment type is required");
    try
    {
      return valueOf(type.toUpperCase(Locale.ENGLISH));
    }
    catch (IllegalArgumentException iae)
    {
      // N.B. we don't pass the iae as the cause of this exception because this happens during invocation, and in that case the
      // enum handler will report the root cause exception rather than the one we throw.
      throw new DataError.Bad("An environment type supplied is invalid");
    }
  }
}
