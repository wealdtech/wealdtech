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

import com.codahale.metrics.Meter;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.wealdtech.DataError;
import com.wealdtech.TwoTuple;
import com.wealdtech.User;
import com.wealdtech.authorisation.UserAuthorisation;
import com.wealdtech.jersey.auth.Authenticator;
import com.wealdtech.jersey.auth.WealdAuthorizer;
import com.wealdtech.jersey.exceptions.UnauthorizedException;
import com.wealdtech.utils.WealdMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Handle authentication and make user available to other providers
 */
public class WealdAuthenticationFilter implements ContainerRequestFilter
{
  private static final Logger LOG = LoggerFactory.getLogger(WealdAuthenticationFilter.class);

  @Context
  private transient HttpServletRequest servletRequest;

  private final transient Authenticator<TwoTuple<User, UserAuthorisation>> basicAuthenticator;
//  private final transient Authenticator<TwoTuple<User, UserAuthorisation>> hawkAuthenticator;
  private final transient Authenticator<TwoTuple<User, UserAuthorisation>> tokenAuthenticator;

  private final Meter attempts;
  private final Meter basicSuccesses;
//  private final Meter hawkSuccesses;
  private final Meter tokenSuccesses;

  @Inject
  public WealdAuthenticationFilter(@Named("basicauth") final Authenticator<TwoTuple<User, UserAuthorisation>> basicAuthenticator,
//                                   @Named("hawkauth") final Authenticator<TwoTuple<User, UserAuthorisation>> hawkAuthenticator,
                                   @Named("tokenauth") final Authenticator<TwoTuple<User, UserAuthorisation>> tokenAuthenticator)
  {
    this.basicAuthenticator = basicAuthenticator;
//    this.hawkAuthenticator = hawkAuthenticator;
    this.tokenAuthenticator = tokenAuthenticator;
    this.attempts = WealdMetrics.getMetricRegistry().meter(name(this.getClass(), "attempts", "requests"));
    this.basicSuccesses = WealdMetrics.getMetricRegistry().meter(name(this.getClass(), "basic-successes", "results"));
//    this.hawkSuccesses = WealdMetrics.getMetricRegistry().meter(name(this.getClass(), "hawk-successes", "results"));
    this.tokenSuccesses = WealdMetrics.getMetricRegistry().meter(name(this.getClass(), "token-successes", "results"));
  }

  @Override
  public ContainerRequest filter(final ContainerRequest request)
  {
    this.attempts.mark();
    Optional<TwoTuple<User, UserAuthorisation>> results = Optional.absent();
    try
    {
//      if (this.hawkAuthenticator.canAuthenticate(request))
//      {
//        results = this.hawkAuthenticator.authenticate(request);
//        if (results.isPresent())
//        {
//          this.hawkSuccesses.mark();
//        }
//      }
//      else
      if (this.basicAuthenticator.canAuthenticate(request))
      {
        results = this.basicAuthenticator.authenticate(request);
        if (results.isPresent())
        {
          this.basicSuccesses.mark();
        }
      }
      else if (this.tokenAuthenticator.canAuthenticate(request))
      {
        results = this.tokenAuthenticator.authenticate(request);
        if (results.isPresent())
        {
          this.tokenSuccesses.mark();
        }
      }
      // Unauthenticated requests are allowed
    }
    catch (final DataError de)
    {
      // Authentication failed
      // We don't pass along the underlying exception as it states the authentication issue,
      // and so provides information to a potential attacker.
      throw new UnauthorizedException("User unknown or authentication failed", "Authentication failed"); // NOPMD
    }

    if (results.isPresent())
    {
      final User authenticatedUser = results.get().getS();
      final UserAuthorisation authorisation= results.get().getT();
      LOG.debug("Authenticated identity \"{}\"", authenticatedUser.getName());

      // We aren't using RBAC so just have an empty set of usergroups
      request.setSecurityContext(new WealdAuthorizer(authenticatedUser.getId()));

      // Store the authenticated user and credentials for later use
      this.servletRequest.setAttribute("com.wealdtech.authenticatedprincipal", authenticatedUser);
      this.servletRequest.setAttribute("com.wealdtech.authenticatedprincipal.authorisation", authorisation);
    }
    return request;
  }
}
