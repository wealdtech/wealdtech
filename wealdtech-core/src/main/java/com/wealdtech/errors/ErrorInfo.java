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
package com.wealdtech.errors;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.wealdtech.DataError;

/**
 * Information regarding an error.
 * <p/>
 * Detailed information to pass back as part of an HTTP response.
 * <p/>This provides a standardized way to return information containing
 * multiple fields to a client
 */
public class ErrorInfo implements Comparable<ErrorInfo>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ErrorInfo.class);

  private final String userMessage;
  private final String developerMessage;
  private final String errorCode;
  private final URI moreInfo;

  /**
   * Create information about an error.
   * @param errorCode the error code.  This should be unique within your application
   * @param userMessage the message suitable for displaying to a user
   * @param developerMessage the message suitable for displaying to a developer
   * @param moreInfo a link to a website that provides additional information
   */
  public ErrorInfo(final String errorCode,
                   final String userMessage,
                   final String developerMessage,
                   final URI moreInfo)
  {
    this.errorCode = errorCode;
    this.userMessage = userMessage;
    this.developerMessage = developerMessage;
    this.moreInfo = moreInfo;
  }

  /**
   * Create information about an error.
   * @param errorCode the error code.  This should be unique within your application
   * @param userMessage the message suitable for displaying to a user
   * @param developerMessage the message suitable for displaying to a developer
   * @param moreInfo a link to a website that provides additional information
   */
  public ErrorInfo(final String errorCode,
                   final String userMessage,
                   final String developerMessage,
                   final String moreInfo)
  {
    this.errorCode = errorCode;
    this.userMessage = userMessage;
    this.developerMessage = developerMessage;
    URI moreInfoUri = null;
    if (moreInfo != null)
    {
      try
      {
        moreInfoUri = new URI(moreInfo);
      }
      catch (URISyntaxException use)
      {
        LOGGER.warn("Failed to parse {} in to a valid URI", moreInfo, use);
        throw new DataError.Bad("Attempt to create error info with bad URI", use);
      }
    }
    this.moreInfo = moreInfoUri;
  }

  /**
   * Obtain the code for this error.
   * @return the error code
   */
  public final String getErrorCode()
  {
    return this.errorCode;
  }

  /**
   * Obtain a message for this error suitable for display to end-users.
   * @return a message
   */
  public final String getUserMessage()
  {
    return this.userMessage;
  }

  /**
   * Obtain a message for this error suitable for display to developers.
   * @return a message
   */
  public final String getDeveloperMessage()
  {
    return this.developerMessage;
  }

  /**
   * Obtain a link to follow for more information on this error.
   * @return a link
   */
  public final URI getMoreInfo()
  {
    return this.moreInfo;
  }

  // Standard object methods follow
  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
                  .add("errorCode", this.errorCode)
                  .add("userMessage", this.userMessage)
                  .add("developerMessage", this.developerMessage)
                  .add("moreInfo", this.moreInfo)
                  .toString();
  }

  @Override
  public boolean equals(final Object that)
  {
    return (that instanceof ErrorInfo) && (this.compareTo((ErrorInfo)that) == 0);
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(this.errorCode, this.userMessage, this.developerMessage, this.moreInfo);
  }

  @Override
  public int compareTo(final ErrorInfo that)
  {
    return ComparisonChain.start()
                          .compare(this.errorCode, that.errorCode, Ordering.natural().nullsFirst())
                          .compare(this.userMessage, that.userMessage, Ordering.natural().nullsFirst())
                          .compare(this.developerMessage, that.developerMessage, Ordering.natural().nullsFirst())
                          .compare(this.moreInfo, that.moreInfo, Ordering.natural().nullsFirst())
                          .result();
  }
}
