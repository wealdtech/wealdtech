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

import com.google.common.base.Splitter;
import com.google.common.net.InetAddresses;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.wealdtech.utils.RequestHint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.Iterator;

/**
 * Pick various items out of the request headers to make available to resources
 */
public class RequestHintFilter implements ContainerRequestFilter
{
  private static final Logger LOG = LoggerFactory.getLogger(RequestHintFilter.class);

  @Context
  HttpServletRequest req;

  @Override
  public ContainerRequest filter(final ContainerRequest request)
  {
    final RequestHint.Builder builder = RequestHint.builder();

    builder.userAgent(request.getHeaderValue("User-Agent"));

    final String geoPosition = request.getHeaderValue("Geo-Position");
    if (geoPosition != null)
    {
      // We only care about items prior to the space (if there is one)
      final Iterator<String> geoItems;
      if (geoPosition.indexOf(' ') != -1)
      {
        geoItems = Splitter.on(";").split(Splitter.on(" ").split(geoPosition).iterator().next()).iterator();
      }
      else
      {
        geoItems = Splitter.on(";").split(geoPosition).iterator();
      }

      if (geoItems.hasNext())
      {
        final String lat = geoItems.next();
        builder.latitude(Float.valueOf(lat));
      }
      if (geoItems.hasNext())
      {
        final String lng = geoItems.next();
        builder.longitude(Float.valueOf(lng));
      }
      if (geoItems.hasNext())
      {
        final String alt = geoItems.next();
        builder.altitude(Float.valueOf(alt));
      }
    }

    try
    {
      builder.address(InetAddresses.forString(req.getRemoteAddr()));
    }
    catch (final IllegalArgumentException ignored)
    {
      LOG.debug("Remote IP address {} could not be parsed", req.getRemoteAddr());
    }

    // Store our completed hint for access by resources
    this.req.setAttribute("com.wealdtech.requesthint", builder.build());

    return request;
  }
}
