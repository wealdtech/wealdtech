/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.auth;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.sun.jersey.spi.container.ContainerRequest;
import com.wealdtech.*;
import com.wealdtech.authentication.*;
import com.wealdtech.authorisation.UserAuthorisation;
import com.wealdtech.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;

import static com.wealdtech.Preconditions.checkNotNull;

/**
 * Authenticate user with token authentication.
 */
public final class WealdTokenAuthenticator extends WealdAuthenticator
{
  private static final Logger LOG = LoggerFactory.getLogger(WealdTokenAuthenticator.class);

  @Inject
  private WealdTokenAuthenticator(final UserService userService)
  {
    super(userService);
  }

  @Override
  public boolean canAuthenticate(final ContainerRequest request)
  {
    boolean result = false;
    try
    {
      obtainRequestCredentials(request);
      result = true;
    }
    catch (final DataError de)
    {
      // This happens if the request is not authenticated using a token
    }
    return result;
  }

  /**
   * Authenticate the user.  If the credentials include an identity ID then this identity is the user which will be passed back (if
   * anything)
   *
   * @return A two-tuple containing the authenticated user and credentials; can be <code>absent</code>
   * @throws ServerError if there is a problem authenticating the credentials
   */
  @Override
  public Optional<TwoTuple<User, UserAuthorisation>> authenticate(final ContainerRequest request)
  {
    final Credentials credentials = obtainRequestCredentials(request);
    final WID<User> identityId = obtainRequestedIdentityId(request);

    final Optional<TwoTuple<User, UserAuthorisation>> result;
    try
    {
      final User unauthenticatedUser = obtainUnauthenticatedUser(credentials);
      if (unauthenticatedUser == null)
      {
        throw new DataError.Bad("Credentials do not relate to a user");
      }

      final UserAuthorisation authorisation = authenticateUser(request, unauthenticatedUser, credentials);

      // At this stage the user has been authenticated
      User user = unauthenticatedUser;

      // The token used to access the user will have been removed by the underlying system

      // Refetch the user with the token removed
      user = userService.obtain(user.getId());

      // Resolve the user to their identity and return them
      result = Optional.of(resolveIdentity(user, authorisation, identityId));
    }
    catch (final DataError.Bad de)
    {
      LOG.debug("Failed to obtain user", de);
      throw de;
    }

    return result;
  }

  @Override
  public Credentials obtainRequestCredentials(final ContainerRequest request)
  {
    // See if the token is passed in using a query parameter
    final MultivaluedMap<String, String> params = request.getQueryParameters();
    String secret = params.getFirst("token");
    if (secret == null)
    {
      // No luck there; check the authorization header
      final String headerValue = request.getHeaderValue(ContainerRequest.AUTHORIZATION);
      if (headerValue.startsWith("Token "))
      {
        secret = headerValue.replace("Token ", "");
      }
    }
    checkNotNull(secret, "Missing token");

    return TokenCredentials.builder().token(secret).build();
  }

  /**
   * Authenticate an unauthenticated user
   *
   * @param user the unauthenticated user
   * @param credentials the credentials used to obtain the user
   *
   * @return Authorisation for the user
   * @throws DataError If the authentication fails
   */
  @Override
  public UserAuthorisation authenticateUser(final ContainerRequest request, final User user, final Credentials credentials)
  {
    final TokenCredentials tokenCredentials = (TokenCredentials)credentials;
    for (final AuthenticationMethod authenticationMethod : user.getAuthenticationMethods())
    {
      if (Objects.equal(authenticationMethod.getType(), TokenCredentials.TOKEN_CREDENTIALS))
      {
        final TokenAuthenticationMethod tokenAuthenticationMethod =
            WObject.recast(authenticationMethod, TokenAuthenticationMethod.class);
        if (Objects.equal(tokenAuthenticationMethod.getToken(), tokenCredentials.getToken()) && !authenticationMethod.hasExpired())
        {
          return UserAuthorisation.builder().userId(user.getId()).scope(tokenAuthenticationMethod.getScope()).build();
        }
      }
    }
    LOG.info("Failed to authenticate user {} with token authentication", user.getId().toString());
    throw new DataError.Authentication("Failed to authenticate user");
  }
}
