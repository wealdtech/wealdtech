/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.filters;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

/**
 * Filter to log requests and how long it takes for them to be processed. Also
 * creates a request ID that is returned to the requestor and can be used to identifiy individual requests
 * track the request.
 */
public class RequestLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter
{
  private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingFilter.class);

  private static final String REQUEST_ID_HEADER = "Request-ID";
  private static final String REQUEST_ID = "REQUEST_ID";
  private static final String REQUEST_START_TIME = "RSTARTTIME";

  @Context
  private HttpServletRequest servletRequest;

  @Override
  public ContainerRequest filter(final ContainerRequest request)
  {
    final String requestId = Long.toHexString(Double.doubleToLongBits(Math.random()));
    MDC.put(REQUEST_ID, requestId);
    MDC.put(REQUEST_START_TIME, String.valueOf(System.currentTimeMillis()));
    if (LOG.isInfoEnabled())
    {
      LOG.info("Started: {} {} from {} ({})", request.getMethod(), request.getRequestUri(), servletRequest.getRemoteAddr(), requestId);
    }

    return request;
  }

  @Override
  public ContainerResponse filter(final ContainerRequest request, final ContainerResponse response)
  {
    try
    {
      final Long startTime = Long.parseLong(MDC.get(REQUEST_START_TIME));
      final String rid = MDC.get(REQUEST_ID);
      final long duration = System.currentTimeMillis() - startTime;
      response.getHttpHeaders().add(REQUEST_ID_HEADER, rid);
      LOG.info("Finished: {} {} [{}] ({} ms)", request.getMethod(), request.getRequestUri(), response.getStatus(), String.valueOf(duration));
    }
    catch (Exception e)
    {
      LOG.warn("Finished {} {} [{}]", request.getMethod(), request.getRequestUri(), response.getStatus());
    }
    return response;
  }
}
