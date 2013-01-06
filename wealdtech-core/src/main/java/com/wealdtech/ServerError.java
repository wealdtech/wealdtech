/*
 *    Copyright 2012 Weald Technology Trading Limited
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

package com.wealdtech;

/**
 * A ServerError covers all exceptions generated due to problems with
 * the server or underlying infrastructure.
 * Such errors suggest that the caller should attempt to carry out
 * the request again at a later date, at which point the server error
 * may have been resolved.
 * <p/>
 * Note that server errors do not provide any information on the validity
 * of the server passed to the method.
 */
public class ServerError extends WealdError
{
  private static final long serialVersionUID = -2991176925799773512L;

  /**
   * Generic server error
   */
  public ServerError()
  {
    super();
  }

  /**
   * Server error with message
   *
   * @param msg
   *          the message
   */
  public ServerError(final String msg)
  {
    super(msg);
  }

  /**
   * Server error with cause
   *
   * @param t
   *          the cause
   */
  public ServerError(final Throwable t)
  {
    super(t);
  }

  /**
   * Server error with message and cause
   *
   * @param msg
   *          the message
   * @param t
   *          the cause
   */
  public ServerError(final String msg, final Throwable t)
  {
    super(msg, t);
  }
}
