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

import com.google.inject.Inject;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.wealdtech.jetty.JettyServerConfiguration;

/**
 * Filter to add a 'Server' header to outgoing responses.
 */
public class ServerHeaderFilter implements ContainerResponseFilter
{
  private static final String SERVERHEADER = "Server";

  private final transient JettyServerConfiguration configuration;

  @Inject
  public ServerHeaderFilter(final JettyServerConfiguration configuration)
  {
    this.configuration = configuration;
  }

  @Override
  public ContainerResponse filter(final ContainerRequest request, final ContainerResponse response)
  {
    response.getHttpHeaders().add(SERVERHEADER, this.configuration.getResponseConfiguration().getServerName());
    return response;
  }
}
