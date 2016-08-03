/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.resources;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.ClientError;
import com.wealdtech.oauth2.OAuth2Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * A generic OAuth2 resource that allows pluggable handlers for different OAuth2 providers.
 */
@Path("oauth2")
@Singleton
public class OAuth2Resource
{
  private static final Logger LOG = LoggerFactory.getLogger(OAuth2Resource.class);

  private final ImmutableMap<String, ? extends OAuth2Handler> handlers;

  @Inject
  public OAuth2Resource(@Named("oauth2handlers") final ImmutableMap<String, ? extends OAuth2Handler> handlers)
  {
    this.handlers = handlers;
  }

  @GET
  @Path("{handler}/auth")
  public Response generateAuthorisationUri(//@Context final User authenticatedUser,
                                           @Context final UriInfo uriInfo,
                                           @PathParam("handler") final String handler)
  {
    if (!handlers.containsKey(handler))
    {
      throw new ClientError("Unknown handler " + handler);
    }
    handlers.get(handler).generateAuthorisationUri(uriInfo.getQueryParameters(true));
    return Response.ok().build();
  }

  @GET
  @Path("{handler}/callback")
  public Response authorisation(@Context final UriInfo uriInfo,
                                @PathParam("handler") final String handler)
  {
    if (!handlers.containsKey(handler))
    {
      throw new ClientError("Unknown handler " + handler);
    }
    handlers.get(handler).handleAuthorisation(uriInfo.getRequestUri());
    return Response.ok().build();
  }
}
