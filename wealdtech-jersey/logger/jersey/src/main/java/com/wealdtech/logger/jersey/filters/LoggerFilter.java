/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.logger.jersey.filters;

import com.google.inject.Inject;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.wealdtech.logger.RequestLogEntry;
import com.wealdtech.logger.services.RequestLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

/**
 * Provide request logging
 */
public class LoggerFilter implements ContainerRequestFilter, ContainerResponseFilter
{
  private static final Logger LOG = LoggerFactory.getLogger(LoggerFilter.class);

  private static final String REQUEST_START_TIME = "logger.request.start.time";

  private final RequestLogService requestLogService;

  @Inject
  public LoggerFilter(final RequestLogService requestLogService)
  {
    this.requestLogService = requestLogService;
  }

  @Context
  private HttpServletRequest servletRequest;

  @Override
  public ContainerRequest filter(final ContainerRequest request)
  {
    // Note the start time
    servletRequest.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());

    return request;
  }

  @Override
  public ContainerResponse filter(final ContainerRequest request, final ContainerResponse response)
  {
    final Long startTime = (Long)servletRequest.getAttribute(REQUEST_START_TIME);
    final RequestLogEntry logEntry = RequestLogEntry.builder()
                                                    .source(servletRequest.getRemoteAddr())
                                                    .timestamp(startTime)
                                                    .method(request.getMethod())
                                                    .path(request.getPath())
                                                    .status(response.getStatus())
                                                    .duration(System.currentTimeMillis() - startTime)
                                                    .build();
    requestLogService.create(logEntry);

    return response;
  }
}
