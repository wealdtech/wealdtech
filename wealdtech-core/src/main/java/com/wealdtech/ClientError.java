/*
 * Copyright 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

/**
 * A ClientError covers all exceptions generated due to problems with client libraries.
 * Such errors might be down to data or status codes returned from a server which are unhandled by the existing client libraries.
 */
public class ClientError extends WealdError
{
  private static final long serialVersionUID = -2991176925799773512L;

  public static final String URL = "http://status.wealdtech.com/";
  public static final String USERMESSAGE = "Our service is experiencing problems; please try again later";

  /**
   * Generic server error
   */
  public ClientError()
  {
    super(null, USERMESSAGE, URL, null);
  }

  /**
   * Client error with explanation
   * @param message the message
   */
  public ClientError(final String message)
  {
    super(message, USERMESSAGE, URL, null);
  }

  /**
   * Client error with cause
   * @param t the cause
   */
  public ClientError(final Throwable t)
  {
    super(null, USERMESSAGE, URL, t);
  }

  /**
   * Client error with explanation and cause
   * @param message the message
   * @param t the cause
   */
  public ClientError(final String message, final Throwable t)
  {
    super(message, USERMESSAGE, URL, t);
  }
}
