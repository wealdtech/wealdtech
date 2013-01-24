/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.wealdtech.jersey.exceptions;

import javax.ws.rs.core.Response.Status;

/**
 * Exception for internal issues.
 */
public class InternalServerException extends HttpException
{
  private static final long serialVersionUID = 5546207767000364431L;

  public InternalServerException(final String errorCode)
  {
    super(Status.INTERNAL_SERVER_ERROR, errorCode);
  }

  public InternalServerException(final Throwable t)
  {
    super(Status.INTERNAL_SERVER_ERROR, t);
  }

  public InternalServerException(final String errorCode, final Throwable t)
  {
    super(Status.INTERNAL_SERVER_ERROR, errorCode, t);
  }
}