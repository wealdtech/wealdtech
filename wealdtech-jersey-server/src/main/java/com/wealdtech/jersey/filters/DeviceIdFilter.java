/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

/**
 * Makes the supplied device ID available to resources
 */
public class DeviceIdFilter implements ContainerRequestFilter
{
  private static final Logger LOG = LoggerFactory.getLogger(DeviceIdFilter.class);

  @Context
  HttpServletRequest req;

  @Override
  public ContainerRequest filter(final ContainerRequest request)
  {
    final String deviceId = request.getHeaderValue("Device-ID");
    if (deviceId != null)
    {
      LOG.trace("Device ID is {}", deviceId);
    }

    // Store our device ID for access by resources
    this.req.setAttribute("com.wealdtech.deviceid", deviceId);

    return request;
  }
}
