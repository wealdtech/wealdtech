/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.limiter.jersey.filters;

import com.google.inject.Inject;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.wealdtech.limiter.LimiterStats;
import com.wealdtech.limiter.services.LimiterStatsService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Provide rate limiting for connections
 */
public class LimiterFilter implements ContainerRequestFilter
{
  private static final Logger LOG = LoggerFactory.getLogger(LimiterFilter.class);

  @Context
  private HttpServletRequest servletRequest;

  private final LimiterStatsService limiterStatsService;

  @Inject
  public LimiterFilter(final LimiterStatsService limiterStatsService)
  {
    this.limiterStatsService = limiterStatsService;
  }

  @Override
  public ContainerRequest filter(final ContainerRequest request)
  {
    final Long timestamp = (new DateTime().getMillis() / 1000l) * 1000l;
    // Obtain the current stats for this request
    LimiterStats stats = limiterStatsService.obtain(timestamp, servletRequest.getRemoteAddr());

    // Update the existing stats item
    stats = LimiterStats.builder(stats).requests(stats.getRequests() + 1).build();
    limiterStatsService.update(stats);

    if (stats.getRequests() > 10)
    {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }

    return request;
  }
}
