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

package com.wealdtech.jersey.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Filter to log requests and how long it takes for them to be processed. Also
 * creates a request ID that is returned to the requestor and can be used to
 * track the request.
 */
public class RequestLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter
{
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilter.class);

  private static final String REQUESTHIDEADER = "Request-ID";
  private static final String REQUESTID = "REQUESTID";
  private static final String REQUESTSTARTTIME = "RSTARTTIME";

  @Override
  public ContainerRequest filter(final ContainerRequest request)
  {
    final String requestid = Long.toHexString(Double.doubleToLongBits(Math.random()));
    MDC.put(REQUESTID, requestid);
    MDC.put(REQUESTSTARTTIME, String.valueOf(System.currentTimeMillis()));
    if (LOGGER.isInfoEnabled())
    {
      LOGGER.info("Started: {} {} ({})", request.getMethod(), request.getPath(), requestid);
    }

    return request;
  }

  @Override
  public ContainerResponse filter(final ContainerRequest request, final ContainerResponse response)
  {
    try
    {
      final Long startTime = Long.parseLong(MDC.get(REQUESTSTARTTIME));
      final String rid = MDC.get(REQUESTID);
      final long duration = System.currentTimeMillis() - startTime;
      response.getHttpHeaders().add(REQUESTHIDEADER, rid);
      LOGGER.info("Finished: {} {} ({} ms)", request.getMethod(), request.getPath(), String.valueOf(duration));
    }
    catch (Exception e)
    {
      LOGGER.warn("Finished {} {}", request.getMethod(), request.getPath());
    }
    return response;
  }
}
