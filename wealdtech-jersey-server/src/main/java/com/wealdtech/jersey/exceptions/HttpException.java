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

import com.google.common.base.Optional;
import com.wealdtech.WealdError;
import com.wealdtech.errors.ErrorInfoMap;

/**
 * Base class for daemon HTTP exceptions that provide additional information to
 * the requestor.
 */
public class HttpException extends WealdError
{
  private static final long serialVersionUID = -2193964229153866237L;

  private final Status status;
  private final Optional<Integer> retryAfter;

  /**
   * Generate an HTTP exception with underlying application exception.
   * @param status an HTTP status to be sent back to the requestor
   * @param message an explanation of the error
   */
  public HttpException(final Status status, final String message)
  {
    super(message, null, null, null);
    this.status = status;
    this.retryAfter = Optional.absent();
  }

  /**
   * Generate an HTTP exception with underlying application exception.
   * @param status an HTTP status to be sent back to the requestor
   * @param t the underlying application exception
   */
  public HttpException(final Status status, final Throwable t)
  {
    super(null, null, null, t);
    this.status = status;
    this.retryAfter = Optional.absent();
  }

  /**
   * Generate an HTTP exception with underlying application exception.
   * @param status an HTTP status to be sent back to the requestor
   * @param message an explanation of the error
   * @param t the underlying application exception
   */
  public HttpException(final Status status, final String message, final Throwable t)
  {
    super(message, null, null, t);
    this.status = status;
    this.retryAfter = Optional.absent();
  }

  /**
   * Generate an HTTP exception with underlying application exception.
   * <p/>The message provided is used as a key to check the error info map
   * for additional information.  To provide this information please see
   * {@link ErrorInfoMap}.
   * @param status an HTTP status to be sent back to the requestor
   * @param message a message to be pased back to the requestor
   * @param retryAfter the number of seconds the requestor should wait before resubmitting the request
   * @param t the underlying application exception
   */
  public HttpException(final Status status, final String message, final Integer retryAfter, final Throwable t)
  {
    super(message, null, null, t);
    this.status = status;
    this.retryAfter = Optional.fromNullable(retryAfter);
  }

  public Status getStatus()
  {
    return this.status;
  }

  public Optional<Integer> getRetryAfter()
  {
    return this.retryAfter;
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

  /**
   * Provide the full path of the throwing class.
   * This comes from the lowest-level WealdError that we can find.
   */
  @Override
  public String getThrowingClassName()
  {
    WealdError we = null;
    Throwable t = this.getCause();
    while (t != null)
    {
      if (t instanceof WealdError)
      {
        we = (WealdError)t;
      }
      t = t.getCause();
    }
    String result = null;
    final StackTraceElement[] stacktrace = we.getStackTrace();
    if (stacktrace.length > 0)
    {
      result = stacktrace[0].getClassName();
    }
    return result;
  }

  /**
   * Provide the name of the throwing method
   */
  @Override
  public String getThrowingMethodName()
  {
    WealdError we = null;
    Throwable t = this.getCause();
    while (t != null)
    {
      if (t instanceof WealdError)
      {
        we = (WealdError)t;
      }
      t = t.getCause();
    }
    String result = null;
    final StackTraceElement[] stacktrace = we.getStackTrace();
    if (stacktrace.length > 0)
    {
      result = stacktrace[0].getMethodName();
    }
    return result;
  }

  public String getFullyQualifiedMessage()
  {
    StringBuilder sb = new StringBuilder(128);
    String className = getThrowingClassName();
    if (className != null)
    {
      sb.append(className);
      sb.append(':');
    }
    String methodName = getThrowingMethodName();
    if (methodName != null)
    {
      sb.append(methodName);
      sb.append(':');
    }
    sb.append(toString());
    return sb.toString();
  }
}
