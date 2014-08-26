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

import com.google.inject.Inject;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.wealdtech.jersey.config.CORSConfiguration;

/**
 * Filter to handle cross-origin resource sharing.
 */
public class CORSFilter implements ContainerResponseFilter
{
  private static final String ORIGINHEADER = "Origin";
  private static final String ACAOHEADER = "Access-Control-Allow-Origin";
  private static final String ACRHHEADER = "Access-Control-Request-Headers";
  private static final String ACAHHEADER = "Access-Control-Allow-Headers";
  private static final String ACAMHEADER = "Access-Control-Allow-Methods";
  private static final String ACACHEADER = "Access-Control-Allow-Credentials";

  private final transient CORSConfiguration configuration;

  @Inject
  public CORSFilter(final CORSConfiguration configuration)
  {
    this.configuration = configuration;
  }

  @Override
  public ContainerResponse filter(final ContainerRequest request, final ContainerResponse response)
  {
    // TODO Tighten up security and configuration options
    if (this.configuration.reflectRequest())
    {
      response.getHttpHeaders().add(ACAOHEADER, request.getHeaderValue(ORIGINHEADER));
    }
    else
    {
      response.getHttpHeaders().add(ACAOHEADER, this.configuration.getOrigin());
    }

    final String requestHeaders = request.getHeaderValue(ACRHHEADER);
    response.getHttpHeaders().add(ACAHHEADER, requestHeaders);

    response.getHttpHeaders().add(ACAMHEADER, this.configuration.getAllowedMethods());

    response.getHttpHeaders().add(ACACHEADER, this.configuration.allowCredentials());

    return response;
  }
}
