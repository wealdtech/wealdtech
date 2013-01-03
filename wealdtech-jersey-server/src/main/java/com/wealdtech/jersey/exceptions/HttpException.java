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
import com.wealdtech.errors.ErrorInfoMap;

/**
 * Base class for daemon HTTP exceptions that provide additional information to
 * the requestor.
 */
public class HttpException extends RuntimeException
{
  private static final long serialVersionUID = -2193964229153866237L;

  private final Status status;
  private final Optional<Integer> retryAfter;

  /**
   * Generate an HTTP exception with underlying application exception.
   * <p/>The message in the application exception is used as a key to check
   * the error info map for additional information.  To provide this information
   * please see {@link ErrorInfoMap}.
   * @param errorCode
   */
  public HttpException(final Status status, final String errorCode)
  {
    super(errorCode);
    this.status = status;
    this.retryAfter = Optional.absent();
  }

  /**
   * Generate an HTTP exception with underlying application exception.
   * <p/>The message in the application exception is used as a key to check
   * the error info map for additional information.  To provide this information
   * please see {@link ErrorInfoMap}.
   * @param status an HTTP status to be sent back to the requestor
   * @param t the underlying application exception
   */
  public HttpException(final Status status, final Throwable t)
  {
    super(t);
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
   * @param t the underlying application exception
   */
  public HttpException(final Status status, final String message, final Throwable t)
  {
    super(message, t);
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
    super(message, t);
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
}
