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
 * Exception for requests which attempt to create an already-existing resource.
 */
public class ConflictException extends HttpException
{
  private static final long serialVersionUID = 2279754347009605253L;

  public static final String USERMESSAGE = "This item already exists";

  public ConflictException(final String message) { this(message, message); }

  public ConflictException(final String message, final String userMessage)
  {
    super(Status.CONFLICT, message, userMessage);
  }

  public ConflictException(final Throwable t)
  {
    super(Status.CONFLICT, t);
  }

  public ConflictException(final String message, final String userMessage, final Throwable t)
  {
    super(Status.CONFLICT, message, userMessage, t);
  }
}
