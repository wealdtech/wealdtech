/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */
package com.wealdtech.jersey.exceptions;

import javax.ws.rs.core.Response.Status;

/**
 * Exception for requests which are not allowed with the current authorization.
 */
public class ForbiddenException extends HttpException
{
  private static final long serialVersionUID = -2302278348355473625L;

  public static final String USERMESSAGE = "You are not allowed to carry out that action";

  public ForbiddenException(final String message) { this(message, message); }

  public ForbiddenException(final String message, final String userMessage)
  {
    super(Status.FORBIDDEN, message, userMessage);
  }

  public ForbiddenException(final Throwable t)
  {
    super(Status.FORBIDDEN, t);
  }

  public ForbiddenException(final String message, final String userMessage, final Throwable t)
  {
    super(Status.FORBIDDEN, message, userMessage, t);
  }
}
