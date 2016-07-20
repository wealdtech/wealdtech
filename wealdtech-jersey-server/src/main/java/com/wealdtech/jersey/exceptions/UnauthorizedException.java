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
 * Exception for requests which require but do not have authorization.
 */
public class UnauthorizedException extends HttpException
{
  private static final long serialVersionUID = 5702671032995783425L;

  public static final String USERMESSAGE = "You are not allowed to access that information";

  public UnauthorizedException(final String message) { this(message, message); }

  public UnauthorizedException(final String message, final String userMessage)
  {
    super(Status.UNAUTHORIZED, message, userMessage);
  }

  public UnauthorizedException(final Throwable t)
  {
    super (Status.UNAUTHORIZED, t);
  }

  public UnauthorizedException(final String message, final String userMessage, final Throwable t)
  {
    super(Status.UNAUTHORIZED, message, userMessage, t);
  }
}
