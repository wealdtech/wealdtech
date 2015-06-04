/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wealdtech.DataError;
import com.wealdtech.utils.StringUtils;

import javax.annotation.Nullable;
import java.util.Locale;

import static com.wealdtech.Preconditions.checkNotNull;

/**
 * The authorisation scope for an authentication method
 */
public enum AuthorisationScope
{
  /**
   * Allow full access
   */
  FULL,
  /**
   * Only allowed to verify an item such as an email address
   */
  VERIFY,
  /**
   * Only allowed to reset a password
   */
  RESET,
  /**
   * Read-write access
   */
  READ_WRITE,
  /**
   * Read-only access
   */
  READ_ONLY,
  /**
   * Read-only free/busy access
   */
  FREE_BUSY,
  /**
   * Allow no access
   */
  NONE;

  @Override
  @JsonValue
  public String toString()
  {
      return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH).replace('_', '-'));
  }

  @JsonCreator
  public static AuthorisationScope fromString(final String scope)
  {
    checkNotNull(scope, "Authorisation scope is required");
    try
    {
      return valueOf(scope.toUpperCase(Locale.ENGLISH).replace('-', '_'));
    }
    catch (final IllegalArgumentException iae)
    {
      // N.B. we don't pass the iae as the cause of this exception because
      // this happens during invocation, and in that case the enum handler
      // will report the root cause exception rather than the one we throw.
      throw new DataError.Bad("An authorization type supplied is invalid"); // NOPMD
    }
  }

  /**
   * Check if a scope is valid for verification operations
   * @param scope the AuthenticationScope
   * @return {@code true} if valid, otherwise {@code false}
   */
  public static boolean canVerify(final AuthorisationScope scope)
  {
    return scope == VERIFY;
  }

  /**
   * Check if a scope is valid to see details
   * @param scope the AuthenticationScope
   * @return {@code true} if valid, otherwise {@code false}
   */
  public static boolean canSeeDetails(@Nullable final AuthorisationScope scope)
  {
    return scope != null && (scope == FULL || scope == READ_WRITE || scope == READ_ONLY);
  }

  /**
   * Check if a scope is valid for read operations
   * @param scope the AuthenticationScope
   * @return {@code true} if valid, otherwise {@code false}
   */
  public static boolean canRead(@Nullable final AuthorisationScope scope)
  {
    return scope != null && (scope == FULL || scope == READ_WRITE || scope == READ_ONLY || scope == FREE_BUSY);
  }

  /**
   * Check if a scope is valid for write operations
   * @param scope the AuthenticationScope
   * @return {@code true} if valid, otherwise {@code false}
   */
  public static boolean canWrite(@Nullable final AuthorisationScope scope)
  {
    return scope != null && (scope == FULL || scope == READ_WRITE);
  }
}
