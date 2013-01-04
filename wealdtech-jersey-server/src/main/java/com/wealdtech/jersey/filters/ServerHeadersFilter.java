package com.wealdtech.jersey.filters;

import com.google.inject.Inject;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Filter to add headers to outgoing responses.
 */
public class ServerHeadersFilter implements ContainerResponseFilter
{
  private static final String SERVERHEADER = "Server";

  @Inject
  public ServerHeadersFilter()
  {
  }

  @Override
  public ContainerResponse filter(final ContainerRequest request, final ContainerResponse response)
  {
    // TODO move to configuration
    response.getHttpHeaders().add(SERVERHEADER, "Weald Technology server");
    return response;
  }
}
