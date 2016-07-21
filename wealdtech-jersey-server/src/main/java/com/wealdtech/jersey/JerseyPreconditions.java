/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey;

import com.wealdtech.jersey.exceptions.BadRequestException;
import com.wealdtech.jersey.exceptions.ForbiddenException;
import com.wealdtech.jersey.exceptions.NotFoundException;
import com.wealdtech.jersey.exceptions.UnauthorizedException;

/**
 * Preconditions that throw exceptions suitable for Jersey when they are not met
 */
public final class JerseyPreconditions
{
  private JerseyPreconditions() {}

  /**
   * Throw a 400 exception if the expression evaluates to {@code false}
   * @param expression the expression
   * @param message the message to send
   */
  public static void checkValidity(final boolean expression, final String message)
  {
    if (!expression)
    {
      throw new BadRequestException(message);
    }
  }

  /**
   * Throw a 401 exception if the expression evaluates to {@code false}
   * @param expression the expression
   */
  public static void checkAuthentication(final boolean expression)
  {
    if (!expression)
    {
      throw new UnauthorizedException("Not authenticated");
    }
  }

  /**
   * Throw a 403 exception if the expression evaluates to {@code false}
   * @param expression the expression
   */
  public static void checkAuthorisation(final boolean expression)
  {
    if (!expression)
    {
      throw new ForbiddenException("Not authorized");
    }
  }

  /**
   * Throw a 404 exception if the object is {@code null}
   * @param obj the object
   * @param message the message to send
   */
  public static void checkPresence(final Object obj, final String message)
  {
    if (obj == null)
    {
      throw new NotFoundException(message);
    }
  }
}
