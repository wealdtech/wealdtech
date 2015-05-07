/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.filters;

import com.google.inject.Inject;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.wealdtech.Application;
import com.wealdtech.ApplicationHeaders;
import com.wealdtech.DataError;
import com.wealdtech.WID;
import com.wealdtech.services.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import static com.wealdtech.Preconditions.checkNotNull;

/**
 */
public class ApplicationFilter implements ContainerRequestFilter
{
  private static final Logger LOG = LoggerFactory.getLogger(ApplicationFilter.class);

  @Context
  private HttpServletRequest servletRequest;

  private ApplicationService applicationService;

  @Inject
  public ApplicationFilter(final ApplicationService applicationService)
  {
    this.applicationService = applicationService;
  }

  @Override
  public ContainerRequest filter(final ContainerRequest request)
  {
    try
    {
      final String applicationIdStr = request.getHeaderValue(ApplicationHeaders.APPLICATION_HEADER);
      final WID<Application> applicationId = WID.fromString(applicationIdStr);
      final Application application = applicationService.obtain(applicationId);
      checkNotNull(application);
      this.servletRequest.setAttribute("com.wealdtech.application", application);
    }
    catch (final DataError ignored)
    {
      // Although we could log this it means that we end up with a line every time someone attempts to access this service without
      // an application ID and in this day and age of
    }
    return request;
  }
}
