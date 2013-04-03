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

import com.google.inject.Inject;
import com.wealdtech.http.JettyServerConfiguration;

/**
 * Exception for requests which the server cannot handle for now.
 */
public class ServiceUnavailableException extends HttpException
{
  private static final long serialVersionUID = -1497533974587152020L;

  public static final String USERMESSAGE = "Our systems are currently unavailable; please try again later";

  @Inject
  private static JettyServerConfiguration configuration;

  public ServiceUnavailableException(final String message, final String userMessage)
  {
    super(Status.SERVICE_UNAVAILABLE, message, userMessage, configuration.getResponseConfiguration().getRetryPeriod(), null);
  }

  public ServiceUnavailableException(final Throwable t)
  {
    super (Status.SERVICE_UNAVAILABLE, null, null, configuration.getResponseConfiguration().getRetryPeriod(), t);
  }

  public ServiceUnavailableException(final String message, final String userMessage, final Throwable t)
  {
    super(Status.SERVICE_UNAVAILABLE, message, userMessage, configuration.getResponseConfiguration().getRetryPeriod(), t);
  }
}
