/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

/**
 * Abstract base exception for all Weald Technology exceptions.
 */
public abstract class WealdError extends RuntimeException
{
  private static final long serialVersionUID = -8127456058290412830L;

  protected static final String BASEURL = "http://developers.wealdtech.com/docs/";

  private final String userMessage;
  private final String url;

  public WealdError(final String message, final String userMessage, final String url, final Throwable t)
  {
    super(message, t);
    this.userMessage = userMessage;
    this.url = url;
  }

  /**
   * Provide the message without the exception name
   */
  @Override
  public String toString()
  {
    final String s = getClass().getName();
    final String message = getLocalizedMessage();
    return (message != null) ? message : s;
  }

  public String getUserMessage()
  {
    return this.userMessage;
  }

  public String getUrl()
  {
    return this.url;
  }

  /**
   * Provide the full path of the throwing class
   */
  public String getThrowingClassName()
  {
    String result = null;
    final StackTraceElement[] stacktrace = this.getStackTrace();
    if (stacktrace.length > 0)
    {
      result = stacktrace[0].getClassName();
    }
    return result;
  }

  /**
   * Provide the name of the throwing method
   */
  public String getThrowingMethodName()
  {
    String result = null;
    final StackTraceElement[] stacktrace = this.getStackTrace();
    if (stacktrace.length > 0)
    {
      result = stacktrace[0].getMethodName();
    }
    return result;
  }
}
