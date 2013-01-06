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
 * A DataError covers all exceptions generated due to bad data. Such errors are
 * always correctable by altering the data presented the the throwing method
 * suitably.
 */
public class DataError extends WealdError
{
  private static final long serialVersionUID = -3487779032177158481L;

  /**
   * Generic data error
   */
  public DataError()
  {
    super();
  }

  /**
   * Data error with message
   *
   * @param msg
   *          the message
   */
  public DataError(final String msg)
  {
    super(msg);
  }

  /**
   * Data error with cause
   *
   * @param t
   *          the cause
   */
  public DataError(final Throwable t)
  {
    super(t);
  }

  /**
   * Data error with message and cause
   *
   * @param msg
   *          the message
   * @param t
   *          the cause
   */
  public DataError(final String msg, final Throwable t)
  {
    super(msg, t);
  }
}
